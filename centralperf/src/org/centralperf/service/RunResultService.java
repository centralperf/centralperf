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
 * Service treating 
 * @since 1.0
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
	 * Save results for a run based on a sample
	 * @param run
	 * @param sample
	 */
    public void saveSample(Run run, Sample sample){
        sample.setRun(run);
        log.debug("Adding Sample :"+sample.getTimestamp());
        sampleRepository.save(sample);
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
            	log.error("Error on CSV parsing:"+e.getMessage(), e1);
            }
        }

        try{
        	sample.setElapsed(new Long(headerInfo.getValue("elapsed",CSVline)));
        }
        catch (NumberFormatException e) {}
        sample.setSampleName(headerInfo.getValue("label",CSVline));
        sample.setStatus(headerInfo.getValue("responseCode",CSVline));
        
        sample.setAssertResult(new Boolean(headerInfo.getValue("success",CSVline)));
        //sample.setAssertResult(new Boolean(headerInfo.getValue("Error",CSVline)));
        String sizeString = headerInfo.getValue("bytes", CSVline);
        
        // TODO : manage format errors
        if(sizeString != null && !"".equals(sizeString.trim())){
        	try{
        		sample.setSizeInOctet(new Long(headerInfo.getValue("bytes", CSVline)));
        	}
        	catch(NumberFormatException exception){}
        }
        else{
        	sample.setSizeInOctet(0);
        }
        
        String latencyString = headerInfo.getValue("latency", CSVline);
        if(latencyString != null  && !"".equals(latencyString.trim())){
        	try{
        		sample.setLatency(new Long(headerInfo.getValue("latency", CSVline)));
        	}
        	catch(NumberFormatException exception){}
        }
        
        String grpThreads = headerInfo.getValue("grpThreads", CSVline);
        if(grpThreads != null) sample.setGrpThreads(new Long(grpThreads));
        String allThreads = headerInfo.getValue("allThreads", CSVline);
        if(allThreads != null) sample.setAllThreads(new Long(allThreads));
       
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
	 * Get info about CSV headers
	 * @return
	 */
	private CSVHeaderInfo getCSVHeaderInfo(){
		if(this.headerInfo == null){headerInfo = new CSVHeaderInfo(csvHeaders.split(JTL_CSV_SEPARATOR));}
		return headerInfo;
	}
	
	public boolean isHeaderLine(String line){
        return isHeaderLine(line.split(JTL_CSV_SEPARATOR));
	}	
	
	public boolean isHeaderLine(String[] line){
        return line.length > 0 && "timestamp".equals(line[0].trim().toLowerCase());
	}
}
