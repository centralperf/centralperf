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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.centralperf.model.dao.Run;
import org.centralperf.model.dao.Sample;
import org.centralperf.service.CSVResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GatlingLogReader should read Gatling tab delimited output file every seconds to extract 
 * data and push them in database.
 * @since 1.0
 */
public class GatlingLogReader extends TimerTask {

	private CSVResultService runResultService;
    
	private static final Logger log = LoggerFactory.getLogger(GatlingLogReader.class);
    
	private File logFilePartialPath;
	
	private File logFile;
    private long logFileLastLength;
    private long nbSamples=0;
    
    private Run run;
    
	private static final String GATLING_SIMULATION_LOG_FILENAME = "simulation.log";
	
	public GatlingLogReader(File logFilePartialPath, CSVResultService runResultService, Run run) {
		this.logFilePartialPath = logFilePartialPath;
		this.run=run;
		this.runResultService=runResultService;
	}

	public static GatlingLogReader newReader(File logFilePartialPath, CSVResultService runResultService, Run run) {
		GatlingLogReader gatlingLogReaderTask = new GatlingLogReader(logFilePartialPath, runResultService, run);
		Timer timer = new Timer();
		
		// repeat the check every second
		timer.schedule(gatlingLogReaderTask, new Date(), 1000);
		
		log.debug("Start reading Gatling tab delimited file every second");
		return gatlingLogReaderTask;
	}
	
	public final void run() {		
		// As Gatling may not have create yet the log file, first check log file exists
		File currentlogFile = detectLogFile();
		
		if(currentlogFile != null){
			log.debug("Start reading of log File " + currentlogFile.getPath());
			processLogChange();
		}
	}

	/**
	 * Detect if the simulation log file has been created
	 * @return null if the log file is not yet ready, the simulation log file otherwise
	 */
	private File detectLogFile(){
		// If log file as been created and detected, return it
		if(logFile != null) return logFile;
		
		String fs = System.getProperty("file.separator");
		// As Gatling add an automatic timestamp at the end of the result folder, have to found it before returning the real log file
		// First check if the output folder has been created and detected
		if(logFilePartialPath.getAbsolutePath().indexOf(GatlingLauncher.OUTPUT_PATTERN_PATH) == -1){
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File file, String name) {
					return name.startsWith(GatlingLauncher.OUTPUT_PATTERN_PATH) && file.isDirectory();
				}
			};
			String[] folders = logFilePartialPath.list(filter);
			if(folders.length > 0)
				logFilePartialPath = new File(logFilePartialPath.getPath() + fs + folders[0]);
			else 
				return null;
		}
		// If we are here, then the output folder exists and is detected. Now search for the simulation log
		File possibleSimulationLogFile = new File(logFilePartialPath.getPath() + fs + GATLING_SIMULATION_LOG_FILENAME);
		if(possibleSimulationLogFile.exists()){
			this.logFile = possibleSimulationLogFile;
			this.logFileLastLength = 0;
			return this.logFile;
		}
		else
			return null;
	}
	
	/**
	 * Triggered when the monitored file has changed
	 * Can be overrided if necessary
	 */
	private void processLogChange() {
		RandomAccessFile access = null;
		long currentFileLength = this.logFile.length();
		if(currentFileLength == this.logFileLastLength) return;
		try {
			log.debug("Processing changes on "+this.logFile.getCanonicalPath());
			access = new RandomAccessFile(this.logFile, "r");
			if (currentFileLength < this.logFileLastLength) {access.seek(currentFileLength);} 
			else {access.seek(this.logFileLastLength);}
		} catch (Exception e) {log.error("Error while reading "+this.logFile.getPath()+": "+e.getMessage(),e);}
		
		String line = null;
		try {
			log.debug("Reading new lines");
			while ((line = access.readLine()) != null) {processLine(line);}
			this.logFileLastLength = currentFileLength;
		} catch (IOException iOE) {log.error("Error while reading line on "+this.logFile.getPath()+": "+iOE.getMessage(),iOE);}

		try {access.close();} 
		catch (IOException iOE){log.error("Error while closing "+this.logFile.getPath()+": "+iOE.getMessage(),iOE);}
	}
	
	/**
	 * Process a line of result generated by Gatling in the result file
	 * Calls the runResultService to add new samples
	 * @param line
	 */
	protected void processLine(String line) {
		log.debug("Processing line : "+line);
		if(line.startsWith("ACTION")){
			String[] parsedLine = line.split("\\t");
			Sample sample = new Sample();
			
			// Compute dates
			Date latencyStartDate = new Date();
			latencyStartDate.setTime(Long.parseLong(parsedLine[4]));
			Date latencyEndDate = new Date();
			latencyEndDate.setTime(Long.parseLong(parsedLine[5]));
			Date responseStartDate = new Date();
			responseStartDate.setTime(Long.parseLong(parsedLine[6]));
			Date responseEndDate = new Date();
			responseEndDate.setTime(Long.parseLong(parsedLine[7]));
			
			sample.setTimestamp(latencyStartDate);
			sample.setElapsed(responseEndDate.getTime() - latencyStartDate.getTime());
			sample.setLatency(latencyEndDate.getTime() - latencyStartDate.getTime());
			sample.setStatus(parsedLine[8]);
			
			sample.setSampleName(parsedLine[3]);
			
			this.nbSamples++;
			log.debug("Saving sample ["+this.nbSamples+"] in run ["+this.run.getId()+"]");
			runResultService.addSample(this.run, sample);
		}
	}
}
