package org.centralperf.service;

import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;

import org.centralperf.helper.JMXScriptVariableExtractor;
import org.centralperf.model.Run;
import org.centralperf.model.ScriptVersion;
import org.centralperf.repository.RunRepository;
import org.centralperf.sampler.api.SamplerLauncher;
import org.centralperf.sampler.api.SamplerRunJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ScriptLauncherService {

	@Resource
	private RunRepository runRepository;
	
	@Resource
	private RunService runService;
	
	@Resource
	private SamplerLauncher samplerLauncher;
	
	private HashMap<Long, SamplerRunJob> runningJobs = new HashMap<Long, SamplerRunJob>();
	
	private static final Logger log = LoggerFactory.getLogger(ScriptLauncherService.class);
	
	public boolean launchRun(Run run){
    	ScriptVersion scriptVersion = run.getScriptVersion();
    	log.debug("Launching run " + run.getLabel());
    	// Replace variables by their value
    	String finalJmxContent = JMXScriptVariableExtractor.replaceVariables(scriptVersion.getJmx(), run.getCustomScriptVariables());
    	SamplerRunJob job = samplerLauncher.launch(finalJmxContent, run);
    	runningJobs.put(run.getId(), job);
    	run.setLaunched(true);
    	run.setRunning(true);
    	run.setStartDate(new Date());
    	runRepository.save(run);
		return true;
	}
	
	public SamplerRunJob getJob(Long runId){
		return runningJobs.get(runId);
	}
	
	/**
	 * Called once the job finished
	 * @param job
	 */
	public void endJob(SamplerRunJob job){
		for(Long runId:runningJobs.keySet()){
			if(runningJobs.get(runId) == job){
				runService.endRun(runId, job);
				runningJobs.remove(runId);
				job.getJmxFile().delete();
				job.getResultFile().delete();
				break;
			}
		}
	}
}
