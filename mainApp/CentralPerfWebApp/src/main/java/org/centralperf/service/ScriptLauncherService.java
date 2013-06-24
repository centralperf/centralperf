package org.centralperf.service;

import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;

import org.centralperf.helper.JMXScriptVariableHelper;
import org.centralperf.helper.JMeterJob;
import org.centralperf.helper.JMeterLauncher;
import org.centralperf.model.Run;
import org.centralperf.model.Script;
import org.centralperf.repository.RunRepository;
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
	private JMeterLauncher jMeterLauncher;
	
	private HashMap<Long, JMeterJob> runningJobs = new HashMap<Long, JMeterJob>();
	
	private static final Logger log = LoggerFactory.getLogger(ScriptLauncherService.class);
	
	public boolean launchRun(Run run){
    	Script script = run.getScript();
    	log.debug("Launching run " + run.getLabel());
    	
    	// Replacing variables
    	String parameterizedScript = JMXScriptVariableHelper.replaceVariableInJMX(run.getCustomScriptVariables(), script.jmx);
    	
    	// Launch run
    	JMeterJob job = jMeterLauncher.launch(parameterizedScript);
    	runningJobs.put(run.getId(), job);
    	run.setLaunched(true);
    	run.setRunning(true);
    	run.setStartDate(new Date());
    	runRepository.save(run);
		return true;
	}
	
	public JMeterJob getJob(Long runId){
		return runningJobs.get(runId);
	}
	
	/**
	 * Called once the job finished
	 * @param job
	 */
	public void endJob(JMeterJob job){
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
