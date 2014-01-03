package org.centralperf.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.centralperf.helper.JMeterJob;
import org.centralperf.helper.view.ExcelView;
import org.centralperf.model.Run;
import org.centralperf.model.RunResultSummary;
import org.centralperf.model.ScriptVariable;
import org.centralperf.model.ScriptVersion;
import org.centralperf.repository.ProjectRepository;
import org.centralperf.repository.RunRepository;
import org.centralperf.repository.ScriptRepository;
import org.centralperf.repository.ScriptVersionRepository;
import org.centralperf.service.GraphService;
import org.centralperf.service.RunResultService;
import org.centralperf.service.RunService;
import org.centralperf.service.ScriptLauncherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.servlet.ModelAndView;

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
	private GraphService graphService;
	
    @Resource
    private ProjectRepository projectRepository;

    @Value("#{appProperties['jmeter.launcher.output.csv.default_headers']}")
    private String csvHeaders;
    
	private static final Logger log = LoggerFactory.getLogger(RunController.class);

    @RequestMapping(value = "/project/{projectId}/run/new", method = RequestMethod.GET)
    public String addRunForm(
            @PathVariable("projectId") Long projectId,
            Model model
    ) {
        model.addAttribute("newRun",new Run());
        model.addAttribute("project", projectRepository.findOne(projectId));
        return "macros/run/new-run-form.macro";
    }
    
    @RequestMapping(value = "/run/new", method = RequestMethod.GET)
    public String addRunFormGlobal(
            Model model
    ) {
        model.addAttribute("newRun",new Run());
        model.addAttribute("projects", projectRepository.findAll());
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
    	model.addAttribute("page","sum");
    	return "runDetail";
    } 
    //FIXME: Use only one with {page} optional
    @RequestMapping(value = "/project/{projectId}/run/{id}/detail/{page}", method = RequestMethod.GET)
    public String showRunDetail(
            @PathVariable("projectId") Long projectId,
            @PathVariable("id") Long id,
            @PathVariable("page") String page,
            Model model){
    	log.debug("Run details for run ["+id+"]");
    	populateModelWithRunInfo(id, model);
    	model.addAttribute("page",page);
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
    public RunStatus getRunOutput(
            @PathVariable("projectId") Long projectId,
            @PathVariable("id") Long id,
            Model model){
    	Run run = runRepository.findOne(id);
    	RunStatus result = new RunStatus();
    	RunResultSummary summary = null;
    	if(run.isRunning()){
	    	JMeterJob job = scriptLauncherService.getJob(run.getId());
	    	if(job != null){
		    	result.setJobOutput(job.getProcessOutput());
		    	// Check that result file exists (not yet if script has just been launched)
	    		if(job.getResultFile().exists()){
			    	try {
						result.setCSVResult(FileUtils.readFileToString(job.getResultFile()));
					} catch (IOException e) {
						e.printStackTrace();
					}
	    		}
				summary = job.getPartialResults();
	    	}
    	}
    	else if(run.isLaunched()){
    		summary = runResultService.getSummaryFromRun(run);
    		result.setJobOutput(run.getProcessOutput());
    	}
    	result.setSummary(summary);
    	result.setRunning(run.isRunning());
    	return result;
    }

    /**
     * Import run from JTL file (action)
     * @param projectId
     * @return
     */
    @RequestMapping(value = "/project/{projectId}/run/import", method = RequestMethod.POST)
    public String importRun(
            @PathVariable("projectId") Long projectId,
            @ModelAttribute("run") @Valid Run run,
            @RequestParam("jtlFile") MultipartFile file,
            BindingResult result,
            Model model) {
        if(result.hasErrors()){
            model.addAttribute("newRun",run);
            return "redirect:/project/" + projectId + "/run";
        }

        // Get the jtl File
        try {
            String jtlContent = new String(file.getBytes());
            runService.importRun(run, jtlContent);
        } catch (IOException e) {
        }

        return "redirect:/project/" + projectId + "/run/" + run.getId() + "/detail";
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
     * get results from a RUN as file (CSV or other)
     * @param projectId
     * @param runId
     * @return
     */
    @RequestMapping(
    		value = {"/project/{projectId}/run/{runId}/results"}, 
    		method = RequestMethod.GET, 
    		produces = "text/csv")
    public ResponseEntity<String> getResultsAsCSV(
            @PathVariable("projectId") Long projectId,
            @PathVariable("runId") Long runId
    		) {

        Run run = runRepository.findOne(runId);

        // Get the CSV file content
        String CSVContent=run.getRunResultCSV();
        // Add headers
        CSVContent = csvHeaders + "\r\n" + CSVContent; 
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Disposition", "attachment; filename=results.csv");
        
        return new ResponseEntity<String>(CSVContent, responseHeaders, HttpStatus.CREATED);
    }    
    
    @Autowired 
    private ApplicationContext applicationContext;
    
    /**
     * Handle request to download an Excel document
     */
    @RequestMapping(value = "/project/{projectId}/run/{runId}/centralperf.xlsx", method = RequestMethod.GET)
    public ModelAndView downloadExcel(
    		ModelAndView mav,
            @PathVariable("projectId") Long projectId,
            @PathVariable("runId") Long runId) {
    	Run run = runRepository.findOne(runId);
    	
    	// get the view and setup
    	ExcelView excelView = applicationContext.getBean(ExcelView.class);
	    excelView.setUrl("/WEB-INF/views/xlsx/centralperf_template");
	    mav.getModel().put("run", run);
    	mav.setView(excelView);
        // return a view which will be resolved by an excel view resolver
        return mav;
    }    
    
    @RequestMapping(value = "/run/{runId}", method = RequestMethod.POST)
    @ResponseBody
    public String updateRun(
                            @PathVariable("runId") Long runId,
                            @RequestParam(value="label", required=false) String label,
                            @RequestParam(value="comment", required=false) String comment
                            ) {
        Run run = runRepository.findOne(runId);
        String valueToReturn = label;
        if(label != null){
        	run.setLabel(label);
        }
        else if(comment != null){
        	run.setComment(comment);
        	valueToReturn = comment;
        }
        runRepository.save(run);
        return valueToReturn;
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
    		model.addAttribute("runGraphSeries",graphService.getSumSeries(run));
    		model.addAttribute("runGraphPie", graphService.getCodeRepartition(run));
    		model.addAttribute("runRTGraph",graphService.getRespTimeSeries(run));
    		model.addAttribute("runSummary",runResultService.getSummaryFromRun(run));

    	}    	
    }
    
    // Inner class to return run results
    class RunStatus{
    	private String jobOutput = "";
    	private String CSVResult = "";
    	private boolean running = false;
    	private RunResultSummary summary;
    	
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
		public RunResultSummary getSummary() {
			return summary;
		}
		public void setSummary(RunResultSummary summary) {
			this.summary = summary;
		}
    }
}
