package org.centralperf.service;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
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

	private static final Logger log = LoggerFactory.getLogger(RunResultService.class);
	
	/**
	 * Save results from CSV to persistence
	 * @param run
	 */
	public void saveResults(Run run){
		String resultInCSV = run.getRunResultCSV();
        saveResults(run, resultInCSV);
	}

    public void saveResults(Run run, String resultInCSV){
        CSVReader csvReader = new CSVReader(new StringReader(resultInCSV));
        String[] nextLine = null;
        CSVHeaderInfo headerInfo = new CSVHeaderInfo(csvHeaders.split(","));
        run.setSamples(new ArrayList<Sample>());
        try {
            while ((nextLine = csvReader.readNext()) != null) {
                int size = nextLine.length;

                // empty line
                if (size == 0) {
                    continue;
                }
                // Header line (for uploaded files)
                if("timestamp".equals(nextLine[0].trim().toLowerCase())){
                    headerInfo = new CSVHeaderInfo(nextLine);
                    continue;
                }

                Sample sample = new Sample();
                try{
                    // Try first to get a timestamp
                    sample.setTimestamp(new Date(new Long(headerInfo.getValue("timestamp",nextLine))));
                }
                catch(NumberFormatException e){
                    // Try to parse this format : 2012/10/30 12:47:47
                    SimpleDateFormat parserSDF=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    try {
                        sample.setTimestamp(parserSDF.parse(headerInfo.getValue("timestamp",nextLine)));
                    } catch (ParseException e1) {
                        // TODO : return parsing error
                    }
                }

                sample.setElapsed(new Long(headerInfo.getValue("elapsed",nextLine)));
                sample.setSampleName(headerInfo.getValue("label",nextLine));
                sample.setStatus(headerInfo.getValue("responseCode",nextLine));
                sample.setAssertResult(new Boolean(headerInfo.getValue("assertionResult",nextLine)));
                sample.setSizeInOctet(new Long(headerInfo.getValue("bytes", nextLine)));
                sample.setLatency(new Long(headerInfo.getValue("latency", nextLine)));
                sample.setRun(run);
                run.getSamples().add(sample);
                log.debug("Adding Sample :"+sample.getSampleName());
            }
        } catch (IOException e) {
            // Impossible to access to the file ?!
        }
        runRepository.save(run);
    }

    private String getColumnValue(String[] CSVHeaders, String[] CSVLine, String columnName){
        return CSVLine[java.util.Arrays.asList(CSVHeaders).indexOf(columnName)].trim();
    }

	public RunResultSummary getSummaryFromRun(Run run){
		if(run.isLaunched()){
			String runResultCVS = run.getRunResultCSV();
			if(runResultCVS != null)
				return getSummaryFromCSVString(run.getRunResultCSV());
			else
				return null;
		}
		else
			return null;
	}
	
	public RunResultSummary getSummaryFromCSVFile(File resultFile) {
		try {
			return getSummaryFromCSVLines(FileUtils.readLines(resultFile));
		} catch (IOException e) {
			return null;
		}
	}

	public RunResultSummary getSummaryFromCSVString(String CSVString) {
		return getSummaryFromCSVLines(Arrays.asList(CSVString.split(System.getProperty("line.separator"))));
	}

	private RunResultSummary getSummaryFromCSVLines(List<String> lines) {
		RunResultSummary summary = new RunResultSummary();
		summary.setNumberOfSample(lines.size());
		if (lines.size() > 0) {
			String lastLine = lines.get(lines.size() - 1);
            try{
			    long lastLineDateAsLong = new Long(lastLine.split(",")[0]);
			    summary.setLastSampleDate(new Date(lastLineDateAsLong));
            }
            catch(NumberFormatException e){
                // TODO : Handle various CSV formats
            }
		}
		return summary;
	}

    private class CSVHeaderInfo{

        private String[] headers;
        private HashMap<String, Integer> headersIndex = new HashMap<String, Integer>();

        public CSVHeaderInfo(String[] headers){
            this.headers = headers;
            for (int i=0; i<headers.length; i++){
                headersIndex.put(headers[i].trim().toLowerCase(), i);
            }
        }

        public String getValue(String headerName, String[] CSVline){
            return CSVline[headersIndex.get(headerName.toLowerCase())];
        }

    }
}
