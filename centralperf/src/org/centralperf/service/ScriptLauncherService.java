package org.centralperf.service;

import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;

import org.centralperf.model.dao.Run;
import org.centralperf.model.dao.ScriptVersion;
import org.centralperf.repository.RunRepository;
import org.centralperf.sampler.api.Sampler;
import org.centralperf.sampler.api.SamplerRunJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ScriptLauncherService {

	@Resource
	private RunRepository runRepository;
	
	@Resource
	private SamplerService samplerService;	
	
	@Resource
	private RunService runService;
	
	private HashMap<Long, SamplerRunJob> runningJobs = new HashMap<Long, SamplerRunJob>();
	
	private static final Logger log = LoggerFactory.getLogger(ScriptLauncherService.class);
	
	public boolean launchRun(Run run){
    	ScriptVersion scriptVersion = run.getScriptVersion();
    	
    	// Get the sampler type
    	Sampler sampler = samplerService.getSamplerByUID(run.getScriptVersion().getScript().getSamplerUID());
    	
    	log.debug("Launching run " + run.getLabel());
    	// Replace variables by their value
    	String finalScriptContent = sampler.getScriptProcessor().replaceVariablesInScript(scriptVersion.getContent(), run.getCustomScriptVariables());
    	   	
    	SamplerRunJob job = sampler.getLauncher().launch(finalScriptContent, run);
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
				job.getSimulationFile().delete();
				job.getResultFile().delete();
				break;
			}
		}
	}
}
