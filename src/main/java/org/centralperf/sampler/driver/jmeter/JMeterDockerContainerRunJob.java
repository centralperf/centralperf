/*
 * Copyright (C) 2014  The Central Perf authors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.centralperf.sampler.driver.jmeter;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;
import org.apache.commons.lang.ArrayUtils;
import org.centralperf.model.dao.Run;
import org.centralperf.sampler.api.SamplerRunJob;
import org.centralperf.sampler.driver.jmeter.helper.DockerHelper;
import org.centralperf.service.CSVResultService;
import org.centralperf.service.ScriptLauncherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A thread to handle Docker based jMeter job launcher and watcher
 *
 * @since 1.2
 */
public class JMeterDockerContainerRunJob implements SamplerRunJob {

	private static final String CONTAINER_PREFIX = "jmeter_centralperf_run_";
	private String[] jmeterCliOptions;
	private Run run;
	private String dockerImage;
	private long startTime;
	private long endTime;
	private boolean running;
	private int exitStatus;
	private String processOutput;
	private File jmxFile;
	private File resultFile;
	private String containerId;

	private ScriptLauncherService scriptLauncherService;
	private CSVResultService runResultService;

	private static final Logger log = LoggerFactory.getLogger(JMeterDockerContainerRunJob.class);

	public JMeterDockerContainerRunJob(String[] jmeterCliOptions, Run run, String dockerImage) {
		this.jmeterCliOptions = jmeterCliOptions;
		this.run = run;
		this.dockerImage = dockerImage;
	}

	@Override
	public void stopProcess() {
		log.debug("Stopping jMeter container");
		if (this.containerId != null) {
			getDockerClient().stopContainerCmd(this.containerId).exec();
			running = false;
		}
	}

	// FIXME : Extract to external provided and check Docker on startup
	private static DockerClient _dockerClient;

	private static DockerClient getDockerClient() {
		if (_dockerClient == null) {
			DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
					.withDockerHost("unix:///var/run/docker.sock")
					.build();
			_dockerClient = DockerClientBuilder.getInstance(config).build();
		}
		return _dockerClient;
	}

	/**
	 * Remove any container for brutaly aborted runs (JVM restart for example)
	 *
	 * @param run The associated run
	 */
	public static void fixIncompleteRun(Run run) {
		getDockerClient()
				.listContainersCmd()
				.withShowAll(true)
				.withNameFilter(Collections.singletonList(buildJMeterContainerNameWithoutTimestamp(run)))
				.exec()
				.stream()
				.findFirst()
				.ifPresent(container -> {
					// TODO : retrieve output and parse it before removing container and wait container completion if still running (resume previous)
					getDockerClient().removeContainerCmd(container.getId()).withForce(true).exec();
					log.info("Container with id '{}' for run with id '{}' was automatically removed after incomplete Run", container.getId(), run.getId());
				});
	}

	/**
	 * Build container name
	 *
	 * @param run Associated run
	 * @return name for the container
	 */
	private static String buildJMeterContainerName(Run run) {
		return buildJMeterContainerNameWithoutTimestamp(run) + "_" + new Date().getTime();
	}

	private static String buildJMeterContainerNameWithoutTimestamp(Run run) {
		return CONTAINER_PREFIX + run.getId();
	}

	@Override
	/**
	 * Launch the jMeter external program
	 */
	public void run() {
		String[] command = new String[]{
				"-n",
				"-t", "/centralperf.jmx",
				"-l", "/logs.csv",
				"-j", "/logs/jmeter.log"
		};

		// FIXME : Create Image if not existing
		DockerClient dockerClient = getDockerClient();
		String containerName = buildJMeterContainerName(run);

		// Check image
		String dockerImageNameWithTag = dockerImage.contains(":") ? dockerImage : dockerImage + ":latest";
		List<Image> matchingImages = dockerClient.listImagesCmd().withImageNameFilter(dockerImageNameWithTag).exec();
		// If missing, try to pull
		if (matchingImages.isEmpty()) {
			log.info("Docker image '{}' is missing. Pulling it ...", dockerImageNameWithTag);
			try {
				dockerClient.pullImageCmd(dockerImageNameWithTag).exec(new PullImageResultCallback()).awaitCompletion();
			} catch (InterruptedException e) {
				throw new RuntimeException(String.format("Unable to pull image %s", dockerImageNameWithTag), e);
			}
		}

		// Create jMeter container
		CreateContainerResponse container = dockerClient.createContainerCmd(dockerImageNameWithTag)
				.withName(containerName)
				.withCmd((String[]) ArrayUtils.addAll(command, jmeterCliOptions))
				.exec();
		this.containerId = container.getId();
		startTime = System.currentTimeMillis();

		// Copy the JMX File to the container. Must be transfered through Docker API as a TAR file
		try {
			DockerHelper.copyFileToContainer(dockerClient, containerId, jmxFile, "centralperf.jmx", "/");
		} catch (IOException e) {
			log.error("Unable to copy jMeter script file to container", e);
			throw new RuntimeException("Unable to copy jMeter script file to container", e);
		}

		// Start container
		dockerClient.startContainerCmd(containerId).exec();

		// JMeter is launched
		running = true;

		// Watching for result file change
		JMeterCSVReader reader = new JMeterCSVReader(runResultService, run);
		LogContainerResultCallback loggingCallback = new LogContainerResultCallback(){
			@Override
			public void onNext(Frame item) {
				reader.processLine(new String(item.getPayload()).trim());
			}
		};
		dockerClient.logContainerCmd(containerId)
				.withStdErr(true)
				.withStdOut(true)
				.withFollowStream(true)
				.withTailAll()
				.exec(loggingCallback);

		try {
			loggingCallback.awaitCompletion();
			endTime = System.currentTimeMillis();
			log.debug("Jmeter container ended at "+endTime+" with exit status ["+exitStatus+"]");
		} catch (InterruptedException e) {
			log.warn("JMeter container run was interrupted before normal end:" + e.getMessage(), e);
		} finally {
			try {
				this.processOutput = DockerHelper.getFileContentFromContainer(dockerClient, containerId, "/logs/jmeter.log");
			} catch (IOException e) {
				log.warn("Unable to get jMeter output logs", e);
			}
			dockerClient.removeContainerCmd(containerId).exec();
			running = false;
			scriptLauncherService.endJob(this);
		}

	}

	public String getProcessOutput() {
		return processOutput;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public int getExitStatus() {
		return exitStatus;
	}

	public void setExitStatus(int exitStatus) {
		this.exitStatus = exitStatus;
	}

	public ScriptLauncherService getScriptLauncherService() {
		return scriptLauncherService;
	}

	public void setScriptLauncherService(
			ScriptLauncherService scriptLauncherService) {
		this.scriptLauncherService = scriptLauncherService;
	}

	public File getSimulationFile() {
		return jmxFile;
	}

	public void setSimulationFile(File jmxFile) {
		this.jmxFile = jmxFile;
	}

	public File getResultFile() {
		return resultFile;
	}

	public void setResultFile(File jtlFile) {
		this.resultFile = jtlFile;
	}

	public CSVResultService getRunResultService() {
		return runResultService;
	}

	public void setRunResultService(CSVResultService runResultService) {
		this.runResultService = runResultService;
	}
	
}
