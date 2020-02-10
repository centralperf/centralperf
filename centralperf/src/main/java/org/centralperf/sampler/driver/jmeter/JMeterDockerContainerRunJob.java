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
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import org.apache.commons.lang.ArrayUtils;
import org.centralperf.model.dao.Run;
import org.centralperf.sampler.api.SamplerRunJob;
import org.centralperf.service.CSVResultService;
import org.centralperf.service.ScriptLauncherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Date;

/**
 * A thread to handle external jMeter job launcher and watcher
 * @since 1.0
 */
public class JMeterDockerContainerRunJob implements SamplerRunJob {

	private String[] jmeterCliOptions;
	private Run run;
	private long startTime;
	private long endTime;
	private boolean running;
	private int exitStatus;
	private StringWriter processOutputWriter = new StringWriter();
	private File jmxFile;
	private File resultFile;
	private JMeterCSVReader JMeterCSVFileReader;
	private String containerId;

	private ScriptLauncherService scriptLauncherService;
	private CSVResultService runResultService;

	private String jmxFilePath;

	private static final Logger log = LoggerFactory.getLogger(JMeterDockerContainerRunJob.class);

	public JMeterDockerContainerRunJob(String jmxFilePath, String jtlFilePath, String[] jmeterCliOptions, Run run) {
		this.jmeterCliOptions = jmeterCliOptions;
		this.run=run;
		this.jmxFilePath = jmxFilePath;
	}

	@Override
	public void stopProcess() {
		log.debug("Stopping jMeter container");
		if(this.containerId != null) {
			getDockerClient().stopContainerCmd(this.containerId).exec();
			if(JMeterCSVFileReader != null){
				JMeterCSVFileReader.cancel();
			}
			running = false;
		}
	}

	// FIXME : Extract to external provided and check Docker on startup
	private static DockerClient _dockerClient;
	private static DockerClient getDockerClient(){
		if(_dockerClient == null) {
			DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
					.withDockerHost("unix:///var/run/docker.sock")
					.build();
			_dockerClient = DockerClientBuilder.getInstance(config).build();
		}
		return _dockerClient;
	}

	@Override
	/**
	 * Launch the jMeter external program
	 */
	public void run() {


		Volume scriptVolume = new Volume("/script/centralperf.jmx");
		Volume jtlVolume = new Volume("/logs/");
		HostConfig hostConfig = new HostConfig()
				.withBinds(
						new Bind(jmxFilePath, scriptVolume),
						new Bind(resultFile.getParentFile().getAbsolutePath(), jtlVolume)
				);

		String[] command = new String[]{
				"-n",
				"-t","/script/centralperf.jmx",
				"-l","/logs/" + resultFile.getName(),
				"-j","/logs/jmeter.log"
		};

		// FIXME : Create Image if not existing
		DockerClient dockerClient = getDockerClient();
		String containerName = "jmeter_centralperf_" + new Date().getTime();
		CreateContainerResponse container = dockerClient.createContainerCmd("jmeter")
				.withName(containerName)
				.withHostConfig(hostConfig)
				.withVolumes(scriptVolume, jtlVolume)
				.withCmd((String[]) ArrayUtils.addAll(command, jmeterCliOptions))
				.exec();
		this.containerId = container.getId();
		startTime = System.currentTimeMillis();
		dockerClient.startContainerCmd(containerId).exec();

		// JMeter is launched
		running = true;

		// Watching for result file change
		JMeterCSVReader JMeterCSVFileReader = JMeterCSVReader.newReader(this.getResultFile(), runResultService, run);

		LogContainerResultCallback loggingCallback = new LogContainerResultCallback(){
			@Override
			public void onNext(Frame item) {
				log.debug(item.toString());
			}
		};

		// this essentially test the since=0 case
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
			log.warn("JMeter container run was interrupted before normal end:"+e.getMessage(),e);
		} finally {
			//Stop File reader task after end of job process
			dockerClient.removeContainerCmd(containerId).exec();
			running = false;
			JMeterCSVFileReader.cancel();
			scriptLauncherService.endJob(this);
		}

	}
	
	public String getProcessOutput() {return processOutputWriter.toString();}

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
