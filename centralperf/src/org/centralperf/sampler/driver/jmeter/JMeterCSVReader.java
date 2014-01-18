package org.centralperf.sampler.driver.jmeter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.centralperf.helper.CSVHeaderInfo;
import org.centralperf.model.Run;
import org.centralperf.model.Sample;
import org.centralperf.service.RunResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JMeterCSVReader should read JMeter CSV output file every seconds to extract 
 * data and push them in database.
 * 
 * @author dclairac
 *
 */
public class JMeterCSVReader extends TimerTask {

	private RunResultService runResultService;
    
	private static final Logger log = LoggerFactory.getLogger(JMeterCSVReader.class);
    
	private File csvFile;
	private long csvFileLastTimestamp;
    private long csvFileLastLength;
    private long nbSamples=0;
    
    private CSVHeaderInfo headerInfo;
    private Run run;
	
	public JMeterCSVReader(File csvFile, RunResultService runResultService, Run run) {
		csvFileLastLength = csvFile.length();
		this.csvFile = csvFile;
		this.csvFileLastTimestamp = csvFile.lastModified();
		this.run=run;
		this.runResultService=runResultService;
	}

	public static JMeterCSVReader newReader(File csvFile, RunResultService runResultService, Run run) {
		JMeterCSVReader jMeterCSVReaderTask = new JMeterCSVReader(csvFile, runResultService, run);
		Timer timer = new Timer();
		
		// repeat the check every second
		timer.schedule(jMeterCSVReaderTask, new Date(), 1000);
		
		log.debug("Start reading JMeter CSV file every second");
		return jMeterCSVReaderTask;
	}
	
	public final void run() {
		log.debug("Start reading of CSV File");
		long tsp = csvFile.lastModified();
		if (this.csvFileLastTimestamp != tsp) {
			this.csvFileLastTimestamp = tsp;
			processCSVChange();
		}
	}

	/**
	 * Triggered when the monitored file has changed
	 * Can be overrided if necessary
	 */
	private void processCSVChange() {
		
		RandomAccessFile access = null;
		try {
			log.debug("Processing changes on "+this.csvFile.getCanonicalPath());
			access = new RandomAccessFile(this.csvFile, "r");
			if (this.csvFile.length() < this.csvFileLastLength) {access.seek(this.csvFile.length());} 
			else {access.seek(this.csvFileLastLength);}
		} catch (Exception e) {log.error("Error while reading "+this.csvFile.getPath()+": "+e.getMessage(),e);}
		
		String line = null;
		try {
			log.debug("Reading new CSV lines");
			while ((line = access.readLine()) != null) {processLine(line);}
			this.csvFileLastLength = this.csvFile.length();
		} catch (IOException iOE) {log.error("Error while reading line on "+this.csvFile.getPath()+": "+iOE.getMessage(),iOE);}

		try {access.close();} 
		catch (IOException iOE){log.error("Error while closing "+this.csvFile.getPath()+": "+iOE.getMessage(),iOE);}
	}
	
	protected void processLine(String line) {
		log.debug("Processing line : "+line);
		if(runResultService.isHeaderLine(line)){headerInfo = new CSVHeaderInfo(line.split(RunResultService.JTL_CSV_SEPARATOR));}
		else{
			Sample sample = runResultService.buildSampleFromCSVLine(headerInfo, line);
			if(sample != null){
				this.nbSamples++;
				log.debug("Saving sample ["+this.nbSamples+"] in run ["+this.run.getId()+"]");
				runResultService.saveSample(this.run, sample);
			}
		}
	}
}
