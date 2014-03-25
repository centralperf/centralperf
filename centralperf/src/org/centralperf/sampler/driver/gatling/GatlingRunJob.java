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
package org.centralperf.sampler.driver.gatling;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;

import org.centralperf.model.dao.Run;
import org.centralperf.sampler.api.SamplerRunJob;
import org.centralperf.service.RunResultService;
import org.centralperf.service.ScriptLauncherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A thread to handle external Gatling job launcher and watcher
 * @since 1.0
 */
public class GatlingRunJob implements SamplerRunJob {

	private String[] command;
	private Run run;
	private long startTime;
	private long endTime;
	private boolean running;
	private int exitStatus;
	private StringWriter processOutputWriter = new StringWriter();
	private File simulationFile;
	private File resultFile;
	private Process p;

	private GatlingLogReader gatlingLogReader;
	
	private ScriptLauncherService scriptLauncherService;
	private RunResultService runResultService;

	private String gatlingLauncherPath;	
	
	private static final Logger log = LoggerFactory.getLogger(GatlingRunJob.class);

	public GatlingRunJob(String[] command, Run run) {
		this.command = command;
		this.run=run;
	}

	@Override
	public void stopProcess() {
		log.debug("Killing jMeter process");
		p.destroy();
	}
	
	@Override
	/**
	 * Launch the jMeter external program
	 */
	public void run() {
		log.debug("Running a new Gatling job with command "+ Arrays.toString(command));
		startTime = System.currentTimeMillis();
		ProcessBuilder pb = new ProcessBuilder(command);
		pb = pb.redirectErrorStream(true);	
		Map<String, String> env = pb.environment();
		env.put("GATLING_HOME", gatlingLauncherPath);
		Process p;
		try {
			p = pb.start();
			// Gatling is launched
			running = true;
			// Listening to Gatling standard output
		    StreamWriter ouputListener = new StreamWriter(p.getInputStream(), new PrintWriter(processOutputWriter, true));		
		    
		    // Watching for result file change
		    gatlingLogReader = GatlingLogReader.newReader(this.getResultFile(), runResultService, run);
		    
		    ouputListener.start();		    
			while (running) {
				try {
					p.waitFor();
					//Stop File reader task after end of job process
					running = false;
					gatlingLogReader.cancel();
					ouputListener.interrupt();
				}catch (InterruptedException iE) {
					log.warn("Gatling run was interrupted before normal end:"+iE.getMessage(),iE);
					p.destroy();
				}
			}
			exitStatus = p.exitValue();
		} catch (IOException iOE) {log.error("Gatling job can't read output file:"+iOE.getMessage(),iOE);}
		endTime = System.currentTimeMillis();
		scriptLauncherService.endJob(this);
		gatlingLogReader = null;
		log.debug("Gatling process ended at "+endTime+" with exit status ["+exitStatus+"]");
	}
	
	public String getProcessOutput() {return processOutputWriter.toString();}	
	
	/**
	 * Internal stream writer to redirect Gatling standard output to a PrintWriter
	 * TODO : export to an utility class...
	 */
	class StreamWriter extends Thread {
		private InputStream in;
		private PrintWriter pw;

		StreamWriter(InputStream in, PrintWriter pw) {
			this.in = in;
			this.pw = pw;
		}

		@Override
		public void run() {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = br.readLine()) != null) {
					pw.println(line);
				}
			} 			
			catch (IOException ioE) {log.warn("Gatling run Output was interrupted:"+ioE.getMessage());}
			catch (Exception e) {log.error("Error while reading Gatling run output:"+e.getMessage(),e);} 
			finally {
				try {br.close();}
				catch (IOException ioE) {log.warn("Gatling run Output BufferedReader was not close:"+ioE.getMessage());}
			}
		}
	}

	public String[] getCommand() {
		return command;
	}

	public void setCommand(String[] command) {
		this.command = command;
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
		return simulationFile;
	}

	public void setSimulationFile(File simulationFile) {
		this.simulationFile = simulationFile;
	}

	public File getResultFile() {
		return resultFile;
	}

	public void setResultFile(File jtlFile) {
		this.resultFile = jtlFile;
	}

	public RunResultService getRunResultService() {
		return runResultService;
	}

	public void setRunResultService(RunResultService runResultService) {
		this.runResultService = runResultService;
	}
	
	public void setGatlingLauncherPath(String gatlingLauncherPath) {
		this.gatlingLauncherPath = gatlingLauncherPath;
	}	
}
