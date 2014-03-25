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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.centralperf.model.dao.Run;
import org.centralperf.model.dao.Sample;
import org.centralperf.model.dao.ScriptVariable;
import org.centralperf.model.dao.ScriptVersion;
import org.centralperf.repository.RunRepository;
import org.centralperf.repository.ScriptVariableRepository;
import org.centralperf.repository.ScriptVersionRepository;
import org.centralperf.sampler.api.SamplerRunJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * All services to manage runs
 */
@Service
public class RunService {

	@Resource
	private RunRepository runRepository;

	@Resource
	private RunResultService runResultService;
	
	@Resource
	private ScriptVariableRepository scriptVariableRepository;

    @Resource
    private ScriptVersionRepository scriptVersionRepository;
    
    @Value("#{appProperties['csv.export.field_separator']}")
    private String csvSeparator;
	
	private static final Logger log = LoggerFactory.getLogger(RunService.class);
	
	public void endRun(Long runId, SamplerRunJob job){
		Run run = runRepository.findOne(runId);
		log.debug("Ending run " + run.getLabel());
		run.setRunning(false);
		run.setEndDate(new Date());
		run.setProcessOutput(job.getProcessOutput());
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
			newRun.setRunning(false);
			newRun.setScriptVersion(run.getScriptVersion());
            newRun.setProject(run.getProject());
            List<ScriptVariable> scriptVariables = run.getCustomScriptVariables();
            if(scriptVariables != null && scriptVariables.size() > 0){
            	List<ScriptVariable> customScriptVariables = new ArrayList<ScriptVariable>();
	            for(ScriptVariable scriptVariable:run.getCustomScriptVariables()){
	            	customScriptVariables.add(scriptVariable.clone());
				}
	            newRun.setCustomScriptVariables(customScriptVariables);
            }
			runRepository.save(newRun);
			return newRun;
		} else {
			return null;
		}
	}
	
	/**
	 * Updates a variable for the run
	 * @param runId			ID of the run
	 * @param newVariable	variable to insert or update
	 */
	public void updateRunVariable(Long runId, ScriptVariable newVariable){
		Run run = runRepository.findOne(runId);
		
		// Searching for variable in current run custom variables
		for(ScriptVariable customVariable : run.getCustomScriptVariables()){
			// Already a custom variable with this name => update it
			if(customVariable.getName().equals(newVariable.getName())){
				customVariable.setValue(newVariable.getValue());
				scriptVariableRepository.save(customVariable);
				return;
			}
		}
		
		// nothing found ? => add the variable to the script
		List<ScriptVariable> customVariables = run.getCustomScriptVariables();
		customVariables.add(newVariable);
		run.setCustomScriptVariables(customVariables);
		runRepository.save(run);
	}

    public List<Run> getLastRuns(){
        return runRepository.findAll(new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "startDate"))).getContent();
    }

    public List<Run> getActiveRuns(){
        return runRepository.findByRunning(true);
    }

    public void insertResultsFromCSV(Run run, String csvContent){
        runResultService.saveResults(run, csvContent);
        runRepository.save(run);
    }

    /**
     * Get run result as a string formatted like CSV 
     * @param run	Target run
     * @return	All samples information as CSV format (comma separated)
     */
    public String getResultAsCSV(Run run){
    	 StringBuilder CSVContent=new StringBuilder("timestamp"+csvSeparator+"elapsed"+csvSeparator+"sampleName"+csvSeparator+"status"+csvSeparator+"returnCode"+csvSeparator+"assertResult"+csvSeparator+"sizeInOctet"+csvSeparator+"grpThreads"+csvSeparator+"allThreads"+csvSeparator+"latency"+csvSeparator+"sampleId"+csvSeparator+"runId\r\n");
         for (Sample sample : run.getSamples()) {
 			CSVContent.append(""+
 					sample.getTimestamp()+csvSeparator+
 					sample.getElapsed()+csvSeparator+
 					sample.getSampleName()+csvSeparator+
 					sample.getStatus()+csvSeparator+ 
 					sample.getReturnCode()+csvSeparator+ 
 					sample.isAssertResult()+csvSeparator+ 
 					sample.getSizeInOctet()+csvSeparator+ 
 					sample.getGrpThreads()+csvSeparator+
 					sample.getAllThreads()+csvSeparator+
 					sample.getLatency()+csvSeparator+
 					sample.getId()+csvSeparator+
 					run.getId()+"\r\n");
 		}    	
        return CSVContent.toString();
    }
    
    /**
     * Import a run from CSV content
     * @param run
     * @param CSVContent
     */
    public void importRun(Run run, String CSVContent){

        // Import CSV
        insertResultsFromCSV(run, CSVContent);

        // Set script version
        ScriptVersion scriptVersion = scriptVersionRepository.findOne(run.getScriptVersion().getId());
        run.setScriptVersion(scriptVersion);

        // Update the run to mark it as launched
        // Set start date for imported CSV files
        run.setStartDate(run.getSamples().get(0).getTimestamp());
        run.setEndDate(run.getSamples().get(run.getSamples().size() - 1 ).getTimestamp());
        run.setLaunched(true);
        run.setProcessOutput("Results uploaded by user on " + new Date());

        // Insert into persistence
        runRepository.save(run);
    }
}
