package org.centralperf.service;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.centralperf.model.Run;
import org.centralperf.model.RunResultSummary;
import org.centralperf.model.Sample;
import org.centralperf.repository.SampleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger log = LoggerFactory.getLogger(RunResultService.class);
	
	/**
	 * Save results from CSV to persistence
	 * @param run
	 */
	public void saveResults(Run run){
		String resultInCSV = run.getRunResultCSV();
		CSVReader csvReader = new CSVReader(new StringReader(resultInCSV));

		String[] nextLine = null;
		try {
			while ((nextLine = csvReader.readNext()) != null) {
			    int size = nextLine.length;
			    log.debug(" SAVE RESULT : SIZE["+size+"]");
			    // empty line
			    if (size == 0) {
			        continue;
			    }
			    
			    Sample sample = new Sample();
			    sample.setRun(run);
			    sample.setTimestamp(new Date(new Long(nextLine[0].trim())));
			    sample.setElapsed(new Long(nextLine[1].trim()));
			    sample.setSampleName(nextLine[2].trim());
			    sample.setStatus(new Integer(nextLine[3].trim()));
			    sample.setAssertResult(new Boolean(nextLine[7].trim()));
			    sample.setSizeInOctet(new Long(nextLine[8].trim()));
			    sample.setLatency(new Long(nextLine[9].trim()));
			    sampleRepository.save(sample);
			    log.debug("Adding Sample :"+sample);
			}
		} catch (IOException e) {
			// Impossible to access to the file ?!
		}
	}
	
	public RunResultSummary getSummaryFromRun(Run run){
		if(run.isLaunched()){
			return getSummaryFromCSVString(run.getRunResultCSV());
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
			long lastLineDateAsLong = new Long(lastLine.split(",")[0]);
			summary.setLastSampleDate(new Date(lastLineDateAsLong));
		}
		return summary;
	}
}
