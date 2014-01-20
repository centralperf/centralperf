package org.centralperf.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.centralperf.helper.view.ExcelView;
import org.centralperf.model.RunDetail;
import org.centralperf.model.dao.Run;
import org.centralperf.model.dao.ScriptVariable;
import org.centralperf.model.dao.ScriptVersion;
import org.centralperf.repository.ProjectRepository;
import org.centralperf.repository.RunRepository;
import org.centralperf.repository.ScriptRepository;
import org.centralperf.repository.ScriptVersionRepository;
import org.centralperf.sampler.api.SamplerRunJob;
import org.centralperf.service.RunResultService;
import org.centralperf.service.RunService;
import org.centralperf.service.RunStatisticsService;
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
	private RunStatisticsService runStatService;
	
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
    	log.debug("Calling saveResult for run ["+id+"] but line is commented");
    	//runResultService.saveResults(run);
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
    public RunDetail getRunOutput(
            @PathVariable("projectId") Long projectId,
            @PathVariable("id") Long id,
            Model model){
    	return runStatService.getRunDetail(id);
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
    	SamplerRunJob job = scriptLauncherService.getJob(run.getId());
    	model.addAttribute("run", run);
    	model.addAttribute("job", job);
    }
}
