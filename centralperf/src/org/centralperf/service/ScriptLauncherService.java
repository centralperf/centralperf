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

/**
 * All operation to launch / stop / monitor launched jobs
 * @since 1.0
 */
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
	
	public void stopRun(Run run){
		if(run.isRunning()){
			log.debug("Stopping run ["+run.getId()+"]:" + run.getLabel());
			SamplerRunJob job = getJob(run.getId());
			if(job!=null){job.stopProcess();}
		}
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
