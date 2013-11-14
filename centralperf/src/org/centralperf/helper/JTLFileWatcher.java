package org.centralperf.helper;

import java.io.File;
import java.util.Date;
import java.util.Timer;

import org.centralperf.helper.filemonitoring.FileChangeWatcher;
import org.centralperf.model.RunResultSummary;
import org.centralperf.model.Sample;
import org.centralperf.service.RunResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Monitors a JTL file and update partial results when new lines are added
 * @author Charles Le Gallic
 */
public class JTLFileWatcher extends FileChangeWatcher {

	private RunResultService runResultService;
	
	private RunResultSummary partialResults;
	private Date startDate;
	private long totalBandwith = 0;
	private long totalResponseTime = 0;
	private long totalLatency = 0;
	private long numberOfErrors = 0;
	private long duration = 0;	
		
	private static final Logger log = LoggerFactory.getLogger(JTLFileWatcher.class);	
	
	public JTLFileWatcher(File file, RunResultService runResultService) {
		super(file);
		this.runResultService = runResultService;
		partialResults = new RunResultSummary();
	}	
	
	public static JTLFileWatcher newWatcher(File file, RunResultService runResultService) {
		JTLFileWatcher fileWatcherTask = new JTLFileWatcher(file, runResultService);
		Timer timer = new Timer();
		// repeat the check every second
		timer.schedule(fileWatcherTask, new Date(), 1000);
		return fileWatcherTask;
	}	
	
	@Override
	protected void processLine(String line) {
		Sample sample = runResultService.buildSampleFromCSVLine(line);

		int numberOfSample = partialResults.getNumberOfSample() + 1;
		if(startDate == null){
			startDate = sample.getTimestamp();
		}
		totalBandwith += sample.getSizeInOctet();
		totalResponseTime += sample.getElapsed();
		totalLatency += sample.getLatency();	
		if(!sample.isAssertResult()){
			numberOfErrors ++;
		}
		duration = sample.getTimestamp().getTime() - startDate.getTime();
		
		// Update partial results
		partialResults.setNumberOfSample(numberOfSample);
		partialResults.setLastSampleDate(sample.getTimestamp());
		partialResults.setAverageLatency(totalLatency / numberOfSample);
		partialResults.setAverageResponseTime(totalResponseTime / numberOfSample);
		partialResults.setTotalBandwith(totalBandwith);
		if(duration > 0){
			partialResults.setCurrentBandwith(totalBandwith / (duration / 1000L));
			partialResults.setRequestPerSecond(numberOfSample / (duration / 1000F));
		}
		partialResults.setErrorRate((100 * numberOfErrors) / numberOfSample);
		
		log.debug(partialResults.toString());
	}
	
	public RunResultSummary getPartialResults(){
		return partialResults;
	}

}
