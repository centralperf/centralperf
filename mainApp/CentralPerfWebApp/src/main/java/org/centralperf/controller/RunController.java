package org.centralperf.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.centralperf.helper.JMeterJob;
import org.centralperf.model.Run;
import org.centralperf.model.RunResultSummary;
import org.centralperf.model.ScriptVariable;
import org.centralperf.repository.RunRepository;
import org.centralperf.repository.ScriptRepository;
import org.centralperf.service.RunResultService;
import org.centralperf.service.RunService;
import org.centralperf.service.ScriptLauncherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.WebRequest;

@Controller
@SessionAttributes
public class RunController {
	
	@Resource
	private RunRepository runRepository;

	@Resource
	private ScriptRepository scriptRepository;
	
	@Resource
	private ScriptLauncherService scriptLauncherService;
	
	@Resource
	private RunResultService runResultService;

	@Resource
	private RunService runService;
	
	private static final Logger log = LoggerFactory.getLogger(RunController.class);
	
	@RequestMapping(value = "/run/new", method = RequestMethod.POST)
    public String addRun(
    		@ModelAttribute("run") @Valid Run run, 
    		BindingResult result,
    		Model model
    		) {
		if(result.hasErrors()){
			model.addAttribute("command",run);
			return "redirect:/run";
		}
		runRepository.save(run);
        return "redirect:/run/" + run.getId() + "/detail";
    }
	
	@RequestMapping(value = "/run/{id}/delete", method = RequestMethod.GET)
    public String deleteRun(@PathVariable("id") Long id) {
		runRepository.delete(id);
        return "redirect:/run";
    }
     
    @RequestMapping("/run")
    public String showRuns(Model model) {
    	log.debug("Displaying runs");
    	model.addAttribute("runs",runRepository.findAll());
    	Sort scriptSort = new Sort(Sort.DEFAULT_DIRECTION, "label");
    	model.addAttribute("scripts",scriptRepository.findAll(scriptSort));
    	model.addAttribute("command",new Run());
        return "runs";
    }
    
    @RequestMapping(value = "/run/{id}/launch", method = RequestMethod.GET)
    public String launchRun(@PathVariable("id") Long id){
    	Run run = runRepository.findOne(id);
    	// If the run has already been launched, then create a new run and
    	if(run.isLaunched()){
    		run = runService.copyRun(id);
    	}
    	scriptLauncherService.launchRun(run);
    	return "redirect:/run/" + run.getId() + "/detail";
    }
    
    @RequestMapping(value = "/run/{id}/saveResults", method = RequestMethod.GET)
    @ResponseBody
    public String saveRunResults(@PathVariable("id") Long id){
    	Run run = runRepository.findOne(id);
    	runResultService.saveResults(run);
    	return "OK";
    }    
    
    @RequestMapping(value = "/run/{id}/detail", method = RequestMethod.GET)
    public String showRunDetail(@PathVariable("id") Long id, Model model){
    	log.debug("Run details for run ["+id+"]");
    	populateModelWithRunInfo(id, model);
    	return "runDetail";
    }    
    
    @RequestMapping(value = "/run/{id}/variables/update", method = RequestMethod.POST)
    @ResponseBody
    public boolean updateRunVariables(
    		@PathVariable("id") Long id, 
    		@RequestParam("name") String variableName,
    		@RequestParam("value") String variableValue,
    		Model model){
    	log.debug("Update variable " + variableName + " with value " +  variableValue + " for run ["+id+"]");
    	ScriptVariable variable = new ScriptVariable();
    	variable.setName(variableName);
    	variable.setValue(variableValue);

    	runService.updateRunVariable(id, variable);
    	
    	return true;
    }        
    
    /**
     * Returns the current output of a running run
     * @param id
     * @return
     */
    @RequestMapping(value = "/run/{id}/output", method = RequestMethod.GET)
    @ResponseBody
    public RunResult getRunOutput(@PathVariable("id") Long id, Model model){
    	Run run = runRepository.findOne(id);
    	RunResult result = new RunResult();
    	JMeterJob job = scriptLauncherService.getJob(run.getId());
    	if(job != null){
	    	result.setJobOutput(job.getProcessOutput());
	    	try {
				result.CSVResult = FileUtils.readFileToString(job.getResultFile());
				RunResultSummary summary = runResultService.getSummaryFromCSVFile(job.getResultFile());
				if(summary.getLastSampleDate() != null){
					result.setLastSampleDate(summary.getLastSampleDate().toString());
				}
				result.setNumberOfSamples(summary.getNumberOfSample());
			} catch (IOException e) {
				// Do nothing, as the file may not be created yet
			}
	    	result.setRunning(run.isRunning());
    	}
    	return result;
    }
    
    /**
     * Set model info depending on run state
     * @param runId
     * @param model
     */
    private void populateModelWithRunInfo(Long runId, Model model){
    	Run run = runRepository.findOne(runId);
    	JMeterJob job = scriptLauncherService.getJob(run.getId());
    	model.addAttribute("run", run);
    	model.addAttribute("job", job);
    	if(run.isRunning() && job != null){
    		model.addAttribute("runSummary",runResultService.getSummaryFromCSVFile(job.getResultFile()));
    	}
    	else if(run.isLaunched()){
    		model.addAttribute("runSummary",runResultService.getSummaryFromRun(run));
    		model.addAttribute("runDurationInSeconds",(run.getEndDate().getTime() - run.getStartDate().getTime()) / 1000);
    	}    	
    }
    
    // Inner class to return run results
    class RunResult{
    	private String jobOutput = "";
    	private String CSVResult = "";
    	private boolean running = false;
    	private long numberOfSamples = 0;
    	private String lastSampleDate = null;
    	
		public String getJobOutput() {
			return jobOutput;
		}
		public void setJobOutput(String jobOutput) {
			this.jobOutput = jobOutput;
		}
		public String getCSVResult() {
			return CSVResult;
		}
		public void setCSVResult(String cSVResult) {
			CSVResult = cSVResult;
		}
		public boolean isRunning() {
			return running;
		}
		public void setRunning(boolean running) {
			this.running = running;
		}
		public long getNumberOfSamples() {
			return numberOfSamples;
		}
		public void setNumberOfSamples(long numberOfSamples) {
			this.numberOfSamples = numberOfSamples;
		}
		public String getLastSampleDate() {
			return lastSampleDate;
		}
		public void setLastSampleDate(String lastSampleDate) {
			this.lastSampleDate = lastSampleDate;
		}
    }
}
