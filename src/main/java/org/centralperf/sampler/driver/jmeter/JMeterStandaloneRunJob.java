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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import org.centralperf.model.dao.Run;
import org.centralperf.sampler.api.SamplerRunJob;
import org.centralperf.service.CSVResultService;
import org.centralperf.service.ScriptLauncherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A thread to handle external jMeter job launcher and watcher
 * @since 1.0
 */
public class JMeterStandaloneRunJob implements SamplerRunJob {

	private String[] command;
	private Run run;
	private long startTime;
	private long endTime;
	private boolean running;
	private int exitStatus;
	private StringWriter processOutputWriter = new StringWriter();
	private File jmxFile;
	private File resultFile;
	private Process p;

	private JMeterCSVReader JMeterCSVFileReader;
	
	private ScriptLauncherService scriptLauncherService;
	private CSVResultService runResultService;

	private static final Logger log = LoggerFactory.getLogger(JMeterStandaloneRunJob.class);

	public JMeterStandaloneRunJob(String[] command, Run run) {
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
		log.debug("Running a new jMeter job with command "+ Arrays.toString(command));
		startTime = System.currentTimeMillis();
		ProcessBuilder pb = new ProcessBuilder(command);
		pb = pb.redirectErrorStream(true);	
		try {
			p = pb.start();
			// JMeter is launched
			running = true;
			// Listening to jMeter standard output
			StreamWriter outputListener = new StreamWriter(p.getInputStream(), new PrintWriter(processOutputWriter, true));		
		    
		    // Watching for result file change
		    JMeterCSVFileReader = JMeterCSVReader.newReader(this.getResultFile(), runResultService, run);
		    
		    outputListener.start();		    
			while (running) {
				try {
					p.waitFor();
					//Stop File reader task after end of job process
					running = false;
					JMeterCSVFileReader.cancel();
					outputListener.interrupt();
				}catch (InterruptedException iE) {
					log.warn("JMeter run was interrupted before normal end:"+iE.getMessage(),iE);
					p.destroy();
				}
			}
			exitStatus = p.exitValue();
		} catch (IOException iOE) {log.error("JMeter job can't read output file:"+iOE.getMessage(),iOE);}
		endTime = System.currentTimeMillis();
		scriptLauncherService.endJob(this);
		JMeterCSVFileReader = null;
		log.debug("Jmeter process ended at "+endTime+" with exit status ["+exitStatus+"]");
	}
	
	public String getProcessOutput() {return processOutputWriter.toString();}	
	
	/**
	 * Internal stream writer to redirect jMeter standard output to a PrintWriter
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
				while ((line = br.readLine()) != null) {pw.println(line);}
			}
			catch (IOException ioE) {log.warn("JMeter run Output was interrupted:"+ioE.getMessage());}
			catch (Exception e) {log.error("Error while reading JMeter run output:"+e.getMessage(),e);} 
			finally {
				try {br.close();}
				catch (IOException ioE) {log.warn("JMeter run Output BufferedReader was not close:"+ioE.getMessage());}
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
		return jmxFile;
	}
	public void setSimulationFile(File simulationFile) {
		this.jmxFile = simulationFile;
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
