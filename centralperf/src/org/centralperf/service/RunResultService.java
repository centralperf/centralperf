package org.centralperf.service;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.centralperf.helper.CSVHeaderInfo;
import org.centralperf.model.Run;
import org.centralperf.model.RunResultSummary;
import org.centralperf.model.Sample;
import org.centralperf.repository.RunRepository;
import org.centralperf.repository.SampleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Service treating 
 * @author charles
 */
@Service
public class RunResultService {

	@Resource
	private SampleRepository sampleRepository;

    @Resource
    private RunRepository runRepository;
    
    @Value("#{appProperties['jmeter.launcher.output.csv.default_headers']}")
    private String csvHeaders;
    
    private CSVHeaderInfo headerInfo;
    
    public static final String JTL_CSV_SEPARATOR = ",";

	private static final Logger log = LoggerFactory.getLogger(RunResultService.class);
	
	/**
	 * Save results from CSV to persistence
	 * @param run
	 */
	public void saveResults(Run run){
		String resultInCSV = run.getRunResultCSV();
        saveResults(run, resultInCSV);
	}

	/**
	 * Save results for a run based on a CSV string (multi line)
	 * @param run
	 * @param resultInCSV
	 */
    public void saveResults(Run run, String resultInCSV){
        CSVReader csvReader = new CSVReader(new StringReader(resultInCSV));
        String[] nextLine = null;
        run.setSamples(new ArrayList<Sample>());
        
        // Loop other CSV lines to build samples
        try {
            while ((nextLine = csvReader.readNext()) != null) {
                int size = nextLine.length;

                // empty line
                if (size == 0) {
                    continue;
                }
                
                // Header line (for uploaded files). Build header info on this header line then skip to next line
                if(isHeaderLine(nextLine)){
                    headerInfo = new CSVHeaderInfo(nextLine);
                    continue;
                }
                
                // Build sample
                Sample sample = buildSampleFromCSVLine(headerInfo, nextLine);
                sample.setRun(run);
                
                run.getSamples().add(sample);
                log.debug("Adding Sample :"+sample.getSampleName());
            }
        } catch (IOException e) {
            // Impossible to access to the file ?!
        }
        runRepository.save(run);
    }

    /**
     * Build a sample based on a CSV line and information about the headers names and orders
     * @param headerInfo	Information about headers
     * @param CSVline		Array of string
     * @return	a sample
     */
    public Sample buildSampleFromCSVLine(CSVHeaderInfo headerInfo, String[] CSVline){
    	if(isHeaderLine(CSVline)){
    		return null;
    	}
    	Sample sample = new Sample();
        try{
            // Try first to get a timestamp
            sample.setTimestamp(new Date(new Long(headerInfo.getValue("timestamp",CSVline))));
        }
        catch(NumberFormatException e){
            // Try to parse this format : 2012/10/30 12:47:47
            SimpleDateFormat parserSDF=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {
                sample.setTimestamp(parserSDF.parse(headerInfo.getValue("timestamp",CSVline)));
            } catch (ParseException e1) {
                // TODO : return parsing error
            }
        }

        try{
        	sample.setElapsed(new Long(headerInfo.getValue("elapsed",CSVline)));
        }
        catch (NumberFormatException e) {}
        sample.setSampleName(headerInfo.getValue("label",CSVline));
        sample.setStatus(headerInfo.getValue("responseCode",CSVline));
        
        String assertResult=headerInfo.getValue("success",CSVline);
        sample.setAssertResult(new Boolean(headerInfo.getValue("success",CSVline)));
        //sample.setAssertResult(new Boolean(headerInfo.getValue("Error",CSVline)));
        String sizeString = headerInfo.getValue("bytes", CSVline);
        
        // TODO : manage format errors
        if(!"".equals(sizeString.trim())){
        	try{
        		sample.setSizeInOctet(new Long(headerInfo.getValue("bytes", CSVline)));
        	}
        	catch(NumberFormatException exception){}
        }
        else{
        	sample.setSizeInOctet(0);
        }
        String latencyString = headerInfo.getValue("latency", CSVline);
        if(!"".equals(latencyString.trim())){
        	try{
        		sample.setLatency(new Long(headerInfo.getValue("latency", CSVline)));
        	}
        	catch(NumberFormatException exception){}
        }
        
        sample.setGrpThreads(new Long(headerInfo.getValue("grpThreads", CSVline)));
        sample.setAllThreads(new Long(headerInfo.getValue("allThreads", CSVline)));
       
        return sample;
    }
    
    public Sample buildSampleFromCSVLine(CSVHeaderInfo headerInfos, String CSVlineAsString){
    	return buildSampleFromCSVLine(headerInfos, CSVlineAsString.split(JTL_CSV_SEPARATOR));
    }    
    
    public Sample buildSampleFromCSVLine(String[] CSVlineAsArray){
    	return buildSampleFromCSVLine(getCSVHeaderInfo(), CSVlineAsArray);
    }

    public Sample buildSampleFromCSVLine(String CSVlineAsString){
    	return buildSampleFromCSVLine(CSVlineAsString.split(JTL_CSV_SEPARATOR));
    }    
    
    /**
     * Compute the main indicators for a run
     * @param run
     * @return a summary instance
     */
	public RunResultSummary getSummaryFromRun(Run run){
		if(run.isLaunched()){
			String runResultCVS = run.getRunResultCSV();
			if(runResultCVS != null)
				return getSummaryFromCSVString(runResultCVS);
			else
				return null;
		}
		else
			return null;
	}
	
	/**
	 * Compute the main indicators from a CVS (JTL) file
	 * @param resultFile a CSV file (Jmeter JTL format)
	 * @return a summary instance
	 */
	public RunResultSummary getSummaryFromCSVFile(File resultFile) {
		try {
			return getSummaryFromCSVLines(FileUtils.readLines(resultFile));
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Compute the main indicators from a CVS String
	 * @param CSVString a CSV formatted string (Jmeter JTL format), mutliple lines
	 * @return a summary instance
	 */
	public RunResultSummary getSummaryFromCSVString(String CSVString) {
		return getSummaryFromCSVLines(Arrays.asList(CSVString.split(System.getProperty("line.separator"))));
	}

	/**
	 * Loop over CSV lines to build a summary
	 * @param lines
	 * @return
	 */
	public RunResultSummary getSummaryFromCSVLines(List<String> lines) {
		RunResultSummary summary = new RunResultSummary();
		int numberOfSample = 0;
		Date startDate = null;
		Date lastSampleDate = null;
		long totalBandwith = 0;
		long totalResponseTime = 0;
		long totalLatency = 0;
		long numberOfErrors = 0;
		long duration = 0;
		long currentUsers = 0;
		long maxUsers = 0;
		
		summary.setNumberOfSample(lines.size());
		for (int i=0; i<lines.size(); i++){
			String[] line = lines.get(i).split(JTL_CSV_SEPARATOR);
			
			// Skip header line if necessary
			if(i==0 && isHeaderLine(line))
				continue;
			
			Sample sample = buildSampleFromCSVLine(getCSVHeaderInfo(), line);
			
			if(startDate == null){
				startDate = sample.getTimestamp();
			}
			
			// update indicators
			numberOfSample++;
			totalBandwith += sample.getSizeInOctet();
			totalResponseTime += sample.getElapsed();
			totalLatency += sample.getLatency();	
			if(!sample.isAssertResult()){
				numberOfErrors ++;
			}
			
			if(maxUsers < sample.getAllThreads())
				maxUsers = sample.getAllThreads();
			
			if(i == lines.size() - 1){
				lastSampleDate = sample.getTimestamp();
				duration = lastSampleDate.getTime() - startDate.getTime();
				currentUsers = sample.getAllThreads();
			}
		}
		summary.setNumberOfSample(numberOfSample);
		summary.setAverageLatency(totalLatency / numberOfSample);
		summary.setAverageResponseTime(totalResponseTime / numberOfSample);
		if(duration > 0){
			summary.setCurrentBandwith(totalBandwith / (duration / 1000));
			summary.setRequestPerSecond(numberOfSample / (duration / 1000F));
			summary.setDuration(duration);
		}
		summary.setErrorRate((100 * numberOfErrors) / numberOfSample);
		summary.setLastSampleDate(lastSampleDate);
		summary.setCurrentUsers(currentUsers);
		summary.setMaxUsers(maxUsers);
		summary.setTotalBandwith(totalBandwith);
		return summary;
	}

	/**
	 * Get info about CSV headers
	 * @return
	 */
	private CSVHeaderInfo getCSVHeaderInfo(){
		if(this.headerInfo == null){
			headerInfo = new CSVHeaderInfo(csvHeaders.split(JTL_CSV_SEPARATOR));
		}
		return headerInfo;
	}
	
	public boolean isHeaderLine(String line){
        return isHeaderLine(line.split(JTL_CSV_SEPARATOR));
	}	
	
	public boolean isHeaderLine(String[] line){
        return line.length > 0 && "timestamp".equals(line[0].trim().toLowerCase());
	}
}
