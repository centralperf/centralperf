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

package org.centralperf.service;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;

import org.centralperf.helper.CSVHeaderInfo;
import org.centralperf.model.SampleDataBackendTypeEnum;
import org.centralperf.model.dao.Run;
import org.centralperf.model.dao.Sample;
import org.centralperf.repository.RunRepository;
import org.centralperf.repository.SampleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Specific service to manage CSV based data for run result
 * Useful to provide CSV based parser for samplers, to export or import results 
 * 
 * @since 1.0
 */
@Service
public class CSVResultService {

	@Resource
	private SampleRepository sampleRepository;
	
	@Resource
	private ElasticSearchService elasticSearchService;

    @Resource
    private RunRepository runRepository;
    
    @Value("${jmeter.launcher.output.csv.default_headers}")
    private String csvHeaders;
    
    private CSVHeaderInfo headerInfo;
    
    @Value("${centralperf.csv.field-separator}")
    private String csvSeparator;

	private static final Logger log = LoggerFactory.getLogger(CSVResultService.class);
	
	/**
	 * Save results to a run with a provided CSV string (multi line)
	 * @param run Run to be fullfil samples for CSV
	 * @param resultInCSV The CSV content to use
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
        	log.error("Problem while parsing CSV String");
        }
        runRepository.save(run);
    }
	
	/**
	 * Save results for a run based on a sample
	 * @param run	Current run
	 * @param sample	new Sample to add
	 */
    public void addSample(Run run, Sample sample){
    	log.debug("Adding Sample :"+sample.getTimestamp());
    	switch(run.getSampleDataBackendType()){
    		case ES:
    			run = runRepository.forceProjectFetch(run.getId());
    			sample.setRun(run);
    			elasticSearchService.insertSample(sample);
    			break;
    		case DEFAULT:
    		default:
    			sample.setRun(run);
    			sampleRepository.save(sample);
    			break;
    	}
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
            sample.setTimestamp(new Date(new Long(headerInfo.getValue(CSVHeaderInfo.CSV_HEADER_TIMESTAMP,CSVline))));
        }
        catch(NumberFormatException e){
            // Try to parse this format : 2012/10/30 12:47:47
            SimpleDateFormat parserSDF=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {
                sample.setTimestamp(parserSDF.parse(headerInfo.getValue(CSVHeaderInfo.CSV_HEADER_TIMESTAMP,CSVline)));
            } catch (ParseException e1) {
            	log.error("Unable to parse timestamp as date. Error = " +e.getMessage() + ". Ignoring this sample...", e1);
            	return null; // TODO use an exception instead
            }
        }

        try{
        	sample.setElapsed(new Long(headerInfo.getValue(CSVHeaderInfo.CSV_HEADER_ELAPSED,CSVline)));
        }
        catch (NumberFormatException e) {}
        sample.setSampleName(headerInfo.getValue(CSVHeaderInfo.CSV_HEADER_SAMPLENAME,CSVline));
        
        String status = headerInfo.getValue(CSVHeaderInfo.CSV_HEADER_STATUS,CSVline);
        if(status != null){
        	sample.setStatus(status);
        }
        
        String assertResult = headerInfo.getValue(CSVHeaderInfo.CSV_HEADER_ASSERTRESULT,CSVline);
        if(assertResult != null){
        	sample.setAssertResult(new Boolean(headerInfo.getValue(CSVHeaderInfo.CSV_HEADER_ASSERTRESULT,CSVline)));
        }
        
        String sizeString = headerInfo.getValue(CSVHeaderInfo.CSV_HEADER_SIZEINBYTES, CSVline);
        
        // TODO : manage format errors
        if(sizeString != null && !"".equals(sizeString.trim())){
        	try{
        		sample.setSizeInOctet(new Long(headerInfo.getValue(CSVHeaderInfo.CSV_HEADER_SIZEINBYTES, CSVline)));
        	}
        	catch(NumberFormatException exception){}
        }
        else{
        	sample.setSizeInOctet(0);
        }
        
        String latencyString = headerInfo.getValue(CSVHeaderInfo.CSV_HEADER_LATENCY, CSVline);
        if(latencyString != null  && !"".equals(latencyString.trim())){
        	try{
        		sample.setLatency(new Long(headerInfo.getValue(CSVHeaderInfo.CSV_HEADER_LATENCY, CSVline)));
        	}
        	catch(NumberFormatException exception){}
        }
        
        String grpThreads = headerInfo.getValue(CSVHeaderInfo.CSV_HEADER_GROUPTHREADS, CSVline);
        if(grpThreads != null) sample.setGrpThreads(new Long(grpThreads));
        String allThreads = headerInfo.getValue(CSVHeaderInfo.CSV_HEADER_ALLTHREADS, CSVline);
        if(allThreads != null) sample.setAllThreads(new Long(allThreads));
       
        return sample;
    }
    
    public Sample buildSampleFromCSVLine(CSVHeaderInfo headerInfos, String CSVlineAsString){
    	return buildSampleFromCSVLine(headerInfos, splitCSVLine(CSVlineAsString,csvSeparator));
    }    
    
    public Sample buildSampleFromCSVLine(String[] CSVlineAsArray){
    	return buildSampleFromCSVLine(getCSVHeaderInfo(), CSVlineAsArray);
    }

    public Sample buildSampleFromCSVLine(String CSVlineAsString){
    	return buildSampleFromCSVLine(splitCSVLine(CSVlineAsString,csvSeparator));
    }    
    
    /**
     * Allow to split a CSV line, taking into account quoted token, like foo,bar,"foo,bar" => ["foo","bar","\"foo,bar\""] instead of ["foo","bar",""\foo","bar\""]
     * Source : http://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
     * @param CSVLine
     * @return
     */
    public static String[] splitCSVLine(String CSVLine, String separator){
    	String[] tokens = CSVLine.split(separator + "(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    	return tokens;
    }
    
	/**
	 * Get info about CSV headers
	 * @return
	 */
	private CSVHeaderInfo getCSVHeaderInfo(){
		if(this.headerInfo == null){headerInfo = new CSVHeaderInfo(csvHeaders.split(csvSeparator));}
		return headerInfo;
	}
	
	/**
	 * Check if the provided line contains headers or sample data  
	 * @param line from the CSV file
	 * @return	true if CSV headers are recognized
	 */
	public boolean isHeaderLine(String line){
        return isHeaderLine(line.split(csvSeparator));
	}	
	
	public boolean isHeaderLine(String[] line){
        return line.length > 0 && CSVHeaderInfo.CSV_HEADER_TIMESTAMP.equals(line[0].trim().toLowerCase());
	}
	
	public String getCsvSeparator() {
		return csvSeparator;
	}

	public void setCsvSeparator(String csvSeparator) {
		this.csvSeparator = csvSeparator;
	}	
}
