package org.centralperf.service;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.centralperf.helper.JMeterJob;
import org.centralperf.model.Run;
import org.centralperf.repository.RunRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RunService {

	@Resource
	private RunRepository runRepository;
	
	@Resource
	private RunResultService runResultService;
	
	private static final Logger log = LoggerFactory.getLogger(RunService.class);
	
	public void endRun(Long runId, JMeterJob job){
		Run run = runRepository.findOne(runId);
		log.debug("Ending run " + run.getLabel());
		run.setRunning(false);
		run.setEndDate(new Date());
		run.setProcessOutput(job.getProcessOutput());
		try {
			run.setRunResultCSV(FileUtils.readFileToString(job.getResultFile()));
			runResultService.saveResults(run);
		} catch (IOException e) {
			e.printStackTrace();
		}
		runRepository.save(run);
	}
	
	/**
	 * Create a copy of a run and save it to the persistence layer (to launch it again for example)
	 * @param runId
	 */
	public Run copyRun(Long runId){
		Run run = runRepository.findOne(runId);
		if(run != null){	
			Run newRun = new Run();
			newRun.setLabel(run.getLabel());
			newRun.setLaunched(false);
			newRun.setRunning(true);
			newRun.setScript(run.getScript());
			runRepository.save(newRun);
			return newRun;
		} else {
			return null;
		}
	}
}
