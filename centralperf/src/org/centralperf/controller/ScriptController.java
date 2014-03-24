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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.centralperf.model.LastScriptVersionLabel;
import org.centralperf.model.dao.Project;
import org.centralperf.model.dao.Run;
import org.centralperf.model.dao.Script;
import org.centralperf.model.dao.ScriptVersion;
import org.centralperf.repository.ProjectRepository;
import org.centralperf.repository.RunRepository;
import org.centralperf.repository.ScriptRepository;
import org.centralperf.repository.ScriptVersionRepository;
import org.centralperf.service.SamplerService;
import org.centralperf.service.ScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * The Script controller handle user interactions with the scripts and their versions
 *  
 * @since 1.0
 * 
 */
@Controller
@SessionAttributes
public class ScriptController {
	
	@Resource
	private ScriptRepository scriptRepository;

	@Resource
	private ScriptVersionRepository scriptVersionRepository;
	
	@Resource
	private ScriptService scriptService;

	@Resource
	private SamplerService samplerService;	
	
	@Resource
	private RunRepository runRepository;

    @Resource
    private ProjectRepository projectRepository;

	private static final Logger log = LoggerFactory.getLogger(ScriptController.class);

	/**
	 * Generic Exception handler for the whole controller
	 * @param exception	The exception raised
	 * @return The message of the exception
	 */
    @ExceptionHandler(Throwable.class)
    public @ResponseBody String handleValidationFailure(Throwable exception) {
        return exception.getMessage();
    }
    
    /**
     * Add a new script to a project
     * @param projectId	ID of the project
     * @param script	Script to add (from HTML form binding)
     * @param file	Uploaded file of the script (JMX or other)
     * @return Redirection to the script detail page
     */
    @RequestMapping(value = "/project/{projectId}/script/new", method = RequestMethod.POST)
    public String addScript(
                            @PathVariable("projectId") Long projectId,
    						@ModelAttribute("script") Script script,
                            @RequestParam("jmxFile") MultipartFile file) {
    	String scriptContent = null;

		// Get the script File
		try {
			scriptContent = new String(file.getBytes());
		} catch (IOException e) {
		}
		Script newScript = scriptService.addScript(script.getProject(), script.getSamplerUID(), script.getLabel(), script.getDescription(), scriptContent);
        return "redirect:/project/" + projectId + "/script/" + newScript.getId() + "/detail";
    }
    
    /**
     * Add a version to a script
     * @param projectId	ID of the project
     * @param scriptId	ID of the script
     * @param scriptVersion	Script version to add (from HTML form binding)
     * @param file Uploaded file of the script (JMX or other)
     * @return Redirection to the script detail page
     */
    @RequestMapping(value = "/project/{projectId}/script/{scriptId}/version/new", method = RequestMethod.POST)
    public String addScriptVersion(
    					@PathVariable("projectId") Long projectId, 
    					@PathVariable("scriptId") Long scriptId, 
    					@ModelAttribute("scriptVersion") ScriptVersion scriptVersion,
    					@RequestParam("scriptFile") MultipartFile file) {
    	String scriptContent = null;

		// Get the script File
		try {
			scriptContent = new String(file.getBytes());
		} catch (IOException e) {
		}
		ScriptVersion newScriptVersion = scriptService.addScriptVersion(scriptRepository.findOne(scriptId), scriptVersion.getDescription(), scriptContent);
        return "redirect:/project/" + projectId + "/script/" + scriptId + "/detail#" + newScriptVersion.getId();
    }    
    
    /**
     * Display all script for a project
     * @param projectId	ID of the project
     * @param model	Model prepared for the script listing view
     * @return Name of the scripts listing view
     */
    @RequestMapping("/project/{projectId}/script")
    public String showProjectScripts(@PathVariable("projectId") Long projectId, Model model) {
        model.addAttribute("newScript",new Script());
        model.addAttribute("scripts",scriptRepository.findAll());
        model.addAttribute("project",projectRepository.findOne(projectId));
        return "scripts";
    }
    
    /**
     * List scripts for a project as JSON
     * @param projectId	ID of the project
     * @return	The script list that will be converted to JSON
     */
	@RequestMapping(value ="/project/{projectId}/script/json/list", method=RequestMethod.GET)  
	public @ResponseBody List<LastScriptVersionLabel> getJsonScriptList(@PathVariable("projectId") Long projectId){
		Project p = projectRepository.findOne(projectId);
		List<LastScriptVersionLabel> lst = new ArrayList<LastScriptVersionLabel>();
		for (Script s : p.getScripts()) {
			List<ScriptVersion> versions = scriptVersionRepository.findByScriptIdOrderByNumberDesc(s.getId());
			ScriptVersion scriptVersion = versions.get(0);
			lst.add(new LastScriptVersionLabel(scriptVersion.getId(), s.getLabel()+" (version "+scriptVersion.getNumber()+")" + " - " + scriptVersion.getScript().getSamplerUID()));
		}
	    return lst;
	}
    
	/**
	 * Display the detail of a script
	 * @param scriptId	ID of the script
	 * @param projectId	ID of the project
	 * @param model	Model prepared for the script detail view
	 * @return Name of the script detail view
	 */
    @RequestMapping(value="/project/{projectId}/script/{scriptId}/detail", method = RequestMethod.GET)
    public String showScriptDetail(
            @PathVariable("scriptId") Long scriptId,
            @PathVariable("projectId") Long projectId,
            Model model){
    	model.addAttribute("script",scriptRepository.findOne(scriptId));
        model.addAttribute("project",projectRepository.findOne(projectId));
        model.addAttribute("scriptVersions",scriptVersionRepository.findByScriptIdOrderByNumberDesc(scriptId));
        model.addAttribute("newScriptVersion",new ScriptVersion());        
        return "scriptDetail";
    }    
    
    /**
     * Delete a script
     * @param scriptId	ID of the script to delete
     * @param projectId	ID of the project
     * @param redirectAttrs	Attributes to display in case of errors
     * @return	Redirection to the script list page for the project
     */
	@RequestMapping(value = "/project/{projectId}/script/{id}/delete", method = RequestMethod.GET)
    public String deleteScript(
            @PathVariable("id") Long scriptId,
            @PathVariable("projectId") Long projectId,
            RedirectAttributes redirectAttrs) {
		List<Run> runsAttached = runRepository.findByScriptVersionScriptId(scriptId);
		if(!runsAttached.isEmpty()){
			redirectAttrs.addFlashAttribute("error","Unable to delete this script because it's attached to at least one run");
		} else{
			scriptRepository.delete(scriptId);
		}
        return "redirect:/project/" + projectId + "/script";
    }
	
	/**
	 * Delete a script version
	 * @param projectId	ID of the project
	 * @param scriptId	ID of the script
	 * @param versionId	ID of the version
	 * @param redirectAttrs		Attributes to display in case of errors
	 * @return Redirection to the script detail page after deletion
	 */
	@RequestMapping(value = "/project/{projectId}/script/{scriptId}/version/{versionId}/delete", method = RequestMethod.GET)
    public String deleteScriptVersion(
    		@PathVariable("projectId") Long projectId,
    		@PathVariable("scriptId") Long scriptId,
            @PathVariable("versionId") Long versionId,
            RedirectAttributes redirectAttrs) {
		List<Run> runsAttached = runRepository.findByScriptVersionId(versionId);
		if(!runsAttached.isEmpty()){
			redirectAttrs.addFlashAttribute("error","Unable to delete this version because it's attached to at least one run");
		} else{
			scriptService.deleteScriptVersion(versionId);
		}
        return "redirect:/project/" + projectId + "/script/" + scriptId + "/detail";
    }	

	/**
	 * List all scripts for all projects
	 * @param model Model prepared for all scripts list view
	 * @return The name of the all scripts view
	 */
    @RequestMapping("/script")
    public String showScripts(Model model) {
        model.addAttribute("scripts",scriptRepository.findAll());
        return "scripts";
    }
    
    /**
     * Update a script (label or description)
     * @param scriptId	ID of the script to update
     * @param label	New label (not updated if null)
     * @param description	New description (not updated if null)
     * @return
     */
    @RequestMapping(value = "/script/{scriptId}", method = RequestMethod.POST)
    @ResponseBody
    public String updateScript(
                            @PathVariable("scriptId") Long scriptId,
                            @RequestParam(value="label",required=false) String label,
                            @RequestParam(value="description",required=false) String description
                            ) {
        Script script = scriptRepository.findOne(scriptId);
        String valueToReturn = label;
        if(label != null){
        	script.setLabel(label);
        }
        else 
        if(description != null){
        	script.setDescription(description);
        	valueToReturn = description;
        }        
        scriptRepository.save(script);
        return valueToReturn;
    }    
    
    // TODO preview should be generated by the injector driver (jMeter or Gatling)
    /**
     * Preview a script (only for JMX). The preview is based on an XSL transformation applied to the JMX XML file, allowing to display it as a javascript tree
     * @param model	Model prepared for the script preview view
     * @param scriptId	ID of the script to preview
     * @param versionNumber
     * @return The name of the XSL view to use
     */
    @RequestMapping(value = "/script/{scriptId}/version/{versionNumber}/preview")
    public String previewScript(
    			Model model,
    			@PathVariable("scriptId") Long scriptId,
    			@PathVariable("versionNumber") int versionNumber
    		) {
    	Script script = scriptRepository.findOne(scriptId);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(script.getVersions().get(versionNumber - 1).getContent())));    	
			model.addAttribute("obj", doc);
		} catch (Exception e) {
			log.error("A problem occured while generating the preview of the script " + e.getMessage());
			e.printStackTrace();
		}
			
		// Parse JMX File
        return "jmeter-test-plan-preview.xsl";
    }     
}
