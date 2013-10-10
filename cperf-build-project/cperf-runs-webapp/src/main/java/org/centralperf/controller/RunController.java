package org.centralperf.controller;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.centralperf.helper.JMeterJob;
import org.centralperf.model.*;
import org.centralperf.repository.ProjectRepository;
import org.centralperf.repository.RunRepository;
import org.centralperf.repository.ScriptRepository;
import org.centralperf.repository.ScriptVersionRepository;
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
import org.springframework.web.multipart.MultipartFile;

@Controller
@SessionAttributes
public class RunController {
	
	@Resource
	private RunRepository runRepository;

	@Resource
	private ScriptRepository scriptRepository;

    @Resource
    private ScriptVersionRepository scriptVersionRepository;

	@Resource
	private ScriptLauncherService scriptLauncherService;
	
	@Resource
	private RunResultService runResultService;

	@Resource
	private RunService runService;

    @Resource
    private ProjectRepository projectRepository;

	private static final Logger log = LoggerFactory.getLogger(RunController.class);

    @RequestMapping(value = "/project/{projectId}/run/new", method = RequestMethod.GET)
    public String createRun(
            @PathVariable("projectId") Long projectId,
            Model model
    ) {
        model.addAttribute("newRun",new Run());
        model.addAttribute("project", projectRepository.findOne(projectId));
        return "macros/run/new-run-form.macro";
    }

	@RequestMapping(value = "/project/{projectId}/run/new", method = RequestMethod.POST)
    public String addRun(
            @PathVariable("projectId") Long projectId,
    		@ModelAttribute("run") @Valid Run run, 
    		BindingResult result,
    		Model model
    		) {
		if(result.hasErrors()){
			model.addAttribute("newRun",run);
			return "redirect:/project/" + projectId + "/run";
		}
        ScriptVersion scriptVersion = scriptVersionRepository.findOne(run.getScriptVersion().getId());
        run.setScriptVersion(scriptVersion);
		runRepository.save(run);
        return "redirect:/project/" + projectId + "/run/" + run.getId() + "/detail";
    }
	
	@RequestMapping(value = "/project/{projectId}/run/{id}/delete", method = RequestMethod.GET)
    public String deleteRun(@PathVariable("id") Long id) {
        Run run = runRepository.findOne(id);
        Long projectId = run.getProject().getId();
		runRepository.delete(id);
        return "redirect:/project/" + projectId + "/detail";
    }
     
    @RequestMapping("/run")
    public String showRuns(Model model) {
    	log.debug("Displaying runs");
    	model.addAttribute("runs",runRepository.findAll());
    	Sort scriptSort = new Sort(Sort.DEFAULT_DIRECTION, "label");
    	model.addAttribute("scripts",scriptRepository.findAll(scriptSort));
    	model.addAttribute("newRun",new Run());
        return "runs";
    }
    
    @RequestMapping(value = "/project/{projectId}/run/{id}/launch", method = RequestMethod.GET)
    public String launchRun(
            @PathVariable("projectId") Long projectId,
            @PathVariable("id") Long id){
    	Run run = runRepository.findOne(id);
    	// If the run has already been launched, then create a new run and
    	if(run.isLaunched()){
    		run = runService.copyRun(id);
    	}
    	scriptLauncherService.launchRun(run);
    	return "redirect:/project/" + projectId + "/run/" + run.getId() + "/detail";
    }

    @RequestMapping(value = "/project/{projectId}/run/{id}/copy", method = RequestMethod.GET)
    public String copyRun(
            @PathVariable("projectId") Long projectId,
            @PathVariable("id") Long id){
        Run newRun = runService.copyRun(id);
        return "redirect:/project/" + projectId + "/run/" + newRun.getId() + "/detail";
    }
    
    @RequestMapping(value = "/project/{projectId}/run/{id}/saveResults", method = RequestMethod.GET)
    @ResponseBody
    public String saveRunResults(
            @PathVariable("projectId") Long projectId,
            @PathVariable("id") Long id){
    	Run run = runRepository.findOne(id);
    	runResultService.saveResults(run);
    	return "OK";
    }    
    
    @RequestMapping(value = "/project/{projectId}/run/{id}/detail", method = RequestMethod.GET)
    public String showRunDetail(
            @PathVariable("projectId") Long projectId,
            @PathVariable("id") Long id,
            Model model){
    	log.debug("Run details for run ["+id+"]");
    	populateModelWithRunInfo(id, model);
    	return "runDetail";
    }    
    
    @RequestMapping(value = "/project/{projectId}/run/{id}/variables/update", method = RequestMethod.POST)
    @ResponseBody
    public boolean updateRunVariables(
            @PathVariable("projectId") Long projectId,
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
    @RequestMapping(value = "/project/{projectId}/run/{id}/output", method = RequestMethod.GET)
    @ResponseBody
    public RunResult getRunOutput(
            @PathVariable("projectId") Long projectId,
            @PathVariable("id") Long id,
            Model model){
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
     * Set results from an uploaded JTL file
     * @param projectId
     * @param runId
     * @param file
     * @param result
     * @return
     */
    @RequestMapping(value = "/project/{projectId}/run/{runId}/results", method = RequestMethod.POST)
    public String uploadResults(
            @PathVariable("projectId") Long projectId,
            @ModelAttribute("runId") Long runId,
            @RequestParam("jtlFile") MultipartFile file,
            BindingResult result) {

        Run run = runRepository.findOne(runId);

        // Get the jtl File
        try {
            String jtlContent = new String(file.getBytes());
            runService.insertResultsFromCSV(run, jtlContent);
        } catch (IOException e) {
        }
        return "redirect:/project/" + projectId + "/run/" + run.getId() + "/detail";
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
