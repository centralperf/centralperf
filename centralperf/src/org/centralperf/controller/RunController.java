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

package org.centralperf.controller;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.centralperf.helper.view.ExcelOOXMLView;
import org.centralperf.model.RunDetail;
import org.centralperf.model.RunDetailGraphTypesEnum;
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

/**
 * The Run controller manage user interactions with the runs
 *  
 * @since 1.0
 * 
 */
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
    
    @Autowired 
    private ApplicationContext applicationContext;    

    @Value("#{appProperties['jmeter.launcher.output.csv.default_headers']}")
    private String csvHeaders;

    @Value("#{appProperties['csv.export.field_separator']}")
    private String csvSeparator;
    
    @Value("#{appProperties['report.cache.delay.seconds']}")
    private Long cacheRefreshDelay;
    
	private static final Logger log = LoggerFactory.getLogger(RunController.class);

	/**
	 * Display the form to create a new Run, for a specific project
	 * @param projectId	ID of the project for this run (from URI)
	 * @param model	Model prepared for the new project form view
	 * @return The path to the new run form view
	 */
    @RequestMapping(value = "/project/{projectId}/run/new", method = RequestMethod.GET)
    public String addRunForm(
            @PathVariable("projectId") Long projectId,
            Model model
    ) {
        model.addAttribute("newRun",new Run());
        model.addAttribute("project", projectRepository.findOne(projectId));
        return "macros/run/new-run-form.macro";
    }
    
    /**
	 * Display the form to create a new Run, no project specified
	 * @param model	Model prepared for the new run form view
     * @return
     */
    @RequestMapping(value = "/run/new", method = RequestMethod.GET)
    public String addRunFormGlobal(
            Model model
    ) {
        model.addAttribute("newRun",new Run());
        model.addAttribute("projects", projectRepository.findAll());
        return "macros/run/new-run-form.macro";
    }    

    /**
     * Create a new Run
     * @param projectId ID of the project (from URI)
     * @param run	The Run to create
     * @param result	BindingResult to check if binding raised some errors
     * @param model	Model prepared for the new project form view in case of errors
     * @return Redirection to the run detail page once the run has been created 
     */
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
	
	/**
	 * Delete a run
	 * @param projectId	ID of the project (from URI)
	 * @param runId	ID of the run to delete  (from URI)
	 * @return Redirection the project detail page
	 */
	@RequestMapping(value = "/project/{projectId}/run/{runId}/delete", method = RequestMethod.GET)
    public String deleteRun(
    		@PathVariable("runId") Long runId,
            @PathVariable("projectId") Long projectId
    		) {
		runRepository.delete(runId);
        return "redirect:/project/" + projectId + "/detail";
    }
     
	/**
	 * Display all runs
	 * @param model	Model prepared for "all runs" view
	 * @return The "all runs" view name
	 */
    @RequestMapping("/run")
    public String showRuns(Model model) {
    	log.debug("Displaying runs");
    	model.addAttribute("runs",runRepository.findAll());
    	Sort scriptSort = new Sort(Sort.DEFAULT_DIRECTION, "label");
    	model.addAttribute("scripts",scriptRepository.findAll(scriptSort));
    	model.addAttribute("newRun",new Run());
        return "runs";
    }
    
    /**
     * Launch a Run
     * @param projectId	ID of the project (from URI)
     * @param runId	ID of the run (from URI)
     * @return	Redirection to the Run detail page
     */
    @RequestMapping(value = "/project/{projectId}/run/{runId}/launch", method = RequestMethod.GET)
    public String launchRun(
            @PathVariable("projectId") Long projectId,
            @PathVariable("runId") Long runId){
    	Run run = runRepository.findOne(runId);
    	// If the run has already been launched, then create a new run and
    	if(run.isLaunched()){
    		run = runService.copyRun(runId);
    	}
    	scriptLauncherService.launchRun(run);
    	return "redirect:/project/" + projectId + "/run/" + run.getId() + "/detail";
    }
    
    /**
     * Stop (abort) a running run
     * @param projectId	ID of the project (from URI)
     * @param runId	ID of the run (from URI)
     * @return	Redirection to the Run detail page once the run has been stopped
     */
    @RequestMapping(value = "/project/{projectId}/run/{runId}/stop", method = RequestMethod.GET)
    public String stopRun(
            @PathVariable("projectId") Long projectId,
            @PathVariable("runId") Long runId){
    	Run run = runRepository.findOne(runId);
    	// If the run has already been launched, then create a new run and
    	if(run.isLaunched()){
    		scriptLauncherService.stopRun(run);
    		String temp=(run.getComment()==null)?"INTERRUPTED BY USER !!!":"INTERRUPTED BY USER !!!\r\n"+run.getComment();
    		run.setComment(temp);
    		runRepository.save(run);
    	}
    	return "redirect:/project/" + projectId + "/run/" + run.getId() + "/detail";
    }

    /**
     * Copy a run
     * @param projectId	ID of the project (from URI)
     * @param rundId	ID of the run to copy (from URI)
     * @return	Redirection to the "copied" run detail page
     */
    @RequestMapping(value = "/project/{projectId}/run/{runId}/copy", method = RequestMethod.GET)
    public String copyRun(
            @PathVariable("projectId") Long projectId,
            @PathVariable("runId") Long rundId){
        Run newRun = runService.copyRun(rundId);
        return "redirect:/project/" + projectId + "/run/" + newRun.getId() + "/detail";
    }
    
    /**
     * Display run detail
     * @param projectId	ID of the project  (from URI)
     * @param runId	ID of the run to display  (from URI)
     * @param model	Model prepared for the run detail view
     * @return	Name of the run detail view
     */
    @RequestMapping(value = "/project/{projectId}/run/{runId}/detail", method = RequestMethod.GET)
    public String showRunDetail(
            @PathVariable("projectId") Long projectId,
            @PathVariable("runId") Long runId,
            Model model){
    	return showRunDetail(projectId, runId, RunDetailGraphTypesEnum.SUMMARY.getPageName(), model);
    } 
    
    /**
     * Display run detail and arrive directly to a specific chart page
     * @param projectId	ID of the project  (from URI)
     * @param runId	ID of the run to display  (from URI)
     * @param page	Name of the chart type (see {@link RunDetailGraphTypesEnum#pageName})
     * @param model Model prepared for the run detail view
     * @return Name of the run detail view
     */
    @RequestMapping(value = "/project/{projectId}/run/{runId}/detail/{page}", method = RequestMethod.GET)
    public String showRunDetail(
            @PathVariable("projectId") Long projectId,
            @PathVariable("runId") Long runId,
            @PathVariable("page") String page,
            Model model){
    	log.debug("Run details for run ["+runId+"]");
    	populateModelWithRunInfo(runId, model);
    	model.addAttribute("page",page);
    	model.addAttribute("refreshDelay",cacheRefreshDelay*1000);
    	return "runDetail";
    }    
    
    /**
     * Update Run script variables set
     * @param projectId	ID of the project  (from URI)
     * @param runId	ID of the run to update  (from URI)
     * @param variableName	Name of the variable to update
     * @param variableValue	New value for the variable
     * @return true if the variable has been updated
     */
    @RequestMapping(value = "/project/{projectId}/run/{runId}/variables/update", method = RequestMethod.POST)
    @ResponseBody
    public boolean updateRunVariables(
            @PathVariable("projectId") Long projectId,
    		@PathVariable("runId") Long runId, 
    		@RequestParam("name") String variableName,
    		@RequestParam("value") String variableValue){
    	log.debug("Update variable " + variableName + " with value " +  variableValue + " for run ["+runId+"]");
    	ScriptVariable variable = new ScriptVariable();
    	variable.setName(variableName);
    	variable.setValue(variableValue);
    	runService.updateRunVariable(runId, variable);
    	return true;
    }        
    
    /**
     * Get output of a running run
     * @param projectId ID of the project  (from URI)
     * @param runId  ID of the run (from URI)
     * @return A bean that will be automatically marshelled to JSON
     */
    @RequestMapping(value = "/project/{projectId}/run/{runId}/output", method = RequestMethod.GET)
    @ResponseBody
    public RunDetail getRunOutput(
            @PathVariable("projectId") Long projectId,
            @PathVariable("runId") Long runId){
    	return runStatService.getRunDetail(runId);
    }

    
    /**
     * Returns the local start time of a run.
     * Local client make request and give it's timestamp.
     * Server compute delta between it's timestamp and client timestamp, and return run start time based on local time
     * @param projectId ID of the project  (From URI)
     * @param runId ID of the run
     * @param ts local timestamp (from client browser)
     * @return Run start time based on local time (compute delta from local and server time if there are not sync)
     */
    @RequestMapping(value = "/project/{projectId}/run/{runId}/startat/{ts}", method = RequestMethod.GET)
    @ResponseBody
    public Long getLocalStartTime(
            @PathVariable("projectId") Long projectId,
            @PathVariable("runId") Long runId,
            @PathVariable("ts") Long ts,
            Model model){
    	Run run = runRepository.findOne(runId);
    	long duration = 0;
    	long lastTime=(new Date()).getTime();
    	if(run!=null && run.isLaunched()){
    		if(!run.isRunning()){lastTime=run.getEndDate().getTime();}
    		duration=lastTime-run.getStartDate().getTime();	
    	}
    	return ts-duration;
    }
    
    
    /**
     * Create a run and import result from JTL (CSV) file
     * @param projectId ID of the project (From URI)
     * @return Redirection to the run detail page
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
     * Set results from an uploaded JTL file for an existing run
     * @param projectId	ID of the project (From URI)
     * @param runId	ID of the run (from URI)
     * @param file	JTL file
     * @return	Redirection to run detail page
     */
    @RequestMapping(value = "/project/{projectId}/run/{runId}/results", method = RequestMethod.POST)
    public String uploadResults(
            @PathVariable("projectId") Long projectId,
            @ModelAttribute("runId") Long runId,
            @RequestParam("jtlFile") MultipartFile file) {

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
     * Download results from a RUN as a file (CSV or other)
     * @param projectId	ID of the project (from URI)
     * @param runId	ID of the run (from URI)
     * @return The JTL/CSV file as text/csv file content type
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
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Disposition", "attachment; filename=results.csv");
        String CSVContent = runService.getResultAsCSV(run);
        return new ResponseEntity<String>(CSVContent, responseHeaders, HttpStatus.CREATED);
    }    
    
    /**
     * Display all samples of a RUN as a HTML page
     * @param projectId	ID of the project (from URI)
     * @param runId	ID of the run (from URI)
     * @return	Name of the view for samples as HTML
     */
    @RequestMapping(
    		value = {
    				"/project/{projectId}/run/{runId}/samples", 
    				"/run/{runId}/samples"
    		}, 
    		method = RequestMethod.GET)
    public String getSamplesAsHTML(
            @PathVariable("projectId") Long projectId,
            @PathVariable("runId") Long runId,
    		Model model
    		) {
    	 Run run = runRepository.findOne(runId);
    	 model.addAttribute("run", run);
    	 return "runSamples";
    }
    
    /**
     * Get run results as an Excel document (XSLX)
     * The file name for now is centralperf.xlsx
     * @param mav	ModelAndView will be used to return an Excel view
     * @param projectId ID of the project (from URI)
     * @param runId ID of the run (from URI)
     * @return A view that will be resolved as an Excel view by the view resolver
     */
    @RequestMapping(value = "/project/{projectId}/run/{runId}/centralperf.xlsx", method = RequestMethod.GET)
    public ModelAndView downloadExcel(
    		ModelAndView mav,
            @PathVariable("projectId") Long projectId,
            @PathVariable("runId") Long runId) {
    	Run run = runRepository.findOne(runId);
    	
    	// get the view and setup
    	ExcelOOXMLView excelView = applicationContext.getBean(ExcelOOXMLView.class);
	    excelView.setUrl("/WEB-INF/views/xlsx/centralperf_template");
	    mav.getModel().put("run", run);
    	mav.setView(excelView);
        // return a view which will be resolved by an excel view resolver
        return mav;
    }    
    
    /**
     * Update run informations (label, comment)
     * @param runId	ID of the run (from URI)
     * @param label	New label (not updated if null)
     * @param comment	New comment (not updated if null)
     * @return
     */
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
     * @param runId ID of the run (from URI)
     * @param model Model to update
     */
    private void populateModelWithRunInfo(Long runId, Model model){
    	Run run = runRepository.findOne(runId);
    	SamplerRunJob job = scriptLauncherService.getJob(run.getId());
    	model.addAttribute("run", run);
    	model.addAttribute("job", job);
    }
}
