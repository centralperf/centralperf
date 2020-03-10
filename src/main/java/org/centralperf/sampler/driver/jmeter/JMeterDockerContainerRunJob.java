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
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.centralperf.model.dao.Run;
import org.centralperf.sampler.api.SamplerRunJob;
import org.centralperf.service.CSVResultService;
import org.centralperf.service.ScriptLauncherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
	private String containerId;

	private ScriptLauncherService scriptLauncherService;
	private CSVResultService runResultService;

	private static final Logger log = LoggerFactory.getLogger(JMeterDockerContainerRunJob.class);

	public JMeterDockerContainerRunJob(String[] jmeterCliOptions, Run run) {
		this.jmeterCliOptions = jmeterCliOptions;
		this.run=run;
	}

	@Override
	public void stopProcess() {
		log.debug("Stopping jMeter container");
		if(this.containerId != null) {
			getDockerClient().stopContainerCmd(this.containerId).exec();
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
		String[] command = new String[]{
				"-n",
				"-t","/centralperf.jmx",
				"-l","/logs.csv",
				"-j","/logs/jmeter.log"
		};

		// FIXME : Create Image if not existing
		DockerClient dockerClient = getDockerClient();
		String containerName = "jmeter_centralperf_" + new Date().getTime();
		// Create jMeter container
		CreateContainerResponse container = dockerClient.createContainerCmd("jmeter")
				.withName(containerName)
				.withCmd((String[]) ArrayUtils.addAll(command, jmeterCliOptions))
				.exec();
		this.containerId = container.getId();
		startTime = System.currentTimeMillis();
		try {
			Path tmp = Files.createTempFile("", ".tar");
			TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(new BufferedOutputStream(Files.newOutputStream(tmp)));
			tarArchiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
			TarArchiveEntry tarArchiveEntry = (TarArchiveEntry) tarArchiveOutputStream.createArchiveEntry(jmxFile, "centralperf.jmx");
			tarArchiveEntry.setSize(jmxFile.length());
			tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);
			try (InputStream in = new FileInputStream(jmxFile)) {
				log.debug("" + IOUtils.copy(in, tarArchiveOutputStream, (int)jmxFile.length()));
			}
			tarArchiveOutputStream.flush();
			tarArchiveOutputStream.closeArchiveEntry();
			tarArchiveOutputStream.finish();
			tarArchiveOutputStream.close();
			dockerClient
					.copyArchiveToContainerCmd(container.getId())
					.withTarInputStream(new BufferedInputStream(new FileInputStream(tmp.toFile())))
					.withRemotePath("/")
					.exec();
		} catch (IOException e){
			e.printStackTrace();
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
			log.warn("JMeter container run was interrupted before normal end:"+e.getMessage(),e);
		} finally {
			dockerClient.removeContainerCmd(containerId).exec();
			running = false;
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
