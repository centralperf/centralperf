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

import java.util.List;

import javax.annotation.Resource;

import org.centralperf.model.dao.Project;
import org.centralperf.model.dao.Run;
import org.centralperf.model.dao.Script;
import org.centralperf.repository.ProjectRepository;
import org.centralperf.service.ProjectService;
import org.centralperf.service.SamplerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * The Project controller manage user interactions with the projects
 *  
 * @since 1.0
 * 
 */
@Controller
public class ProjectController {
	
	private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    @Resource
    private ProjectService projectService;

	@Resource
	private SamplerService samplerService;	    
    
    @Resource
    private ProjectRepository projectRepository;

    /**
     * Create a new project
     * @param project	The new project to create
     * @return The path to the detail page of the project
     */
	@RequestMapping(value = "/project/new", method = RequestMethod.POST)
    public String addProject(
    						@ModelAttribute("project") Project project) {
        projectService.addProject(project);
        return "redirect:/project/" + project.getId() + "/detail";
    }
	
	/**
	 * List current projects as JSON 
	 * @param model
	 * @return
	 */
	@RequestMapping(value ="/project/json/list", method=RequestMethod.GET)  
	public @ResponseBody List<Project> getJsonProjectList(){
		return projectRepository.findProjectsWithScript();
	}
	
	/**
	 * Display the page with all projects 
	 * @param model Model prepared for the projects page view
	 * @return Name of the "all projects" view 
	 */	
    @RequestMapping("/project")
    public String showProjects(Model model) {
    	model.addAttribute("projects",projectRepository.findAll());
    	model.addAttribute("newProject",new Project());
        return "projects";
    }
    
    /**
     * Display the detail of a project
     * @param id	ID of the project
     * @param model	 Model prepared for the project detail page view
     * @return	Name of the "project detail" view
     */
    @RequestMapping(value="/project/{id}/detail", method = RequestMethod.GET)
    @Transactional(readOnly = true) 
    public String showProjectDetail(@PathVariable("id") Long id, Model model){
    	log.debug("project details for project ["+id+"]");
        Project project = projectRepository.findOne(id);
    	model.addAttribute("project",project);
        model.addAttribute("runs",projectService.getLastRuns(project));
        model.addAttribute("scripts",project.getScripts());
        model.addAttribute("samplers",samplerService.getSamplers());
        model.addAttribute("newScript",new Script());
        model.addAttribute("newRun",new Run());
        return "projectDetail";
    }    
    
    /**
     * Delete a project
     * @param id	ID of the project to delete
     * @param redirectAttrs	Any attribute to display if there are any errors
     * @return	Redirection after the project has been deleted (or not)
     */
	@RequestMapping(value = "/project/{id}/delete", method = RequestMethod.GET)
    public String deleteProject(@PathVariable("id") Long id, RedirectAttributes redirectAttrs) {
        Project project = projectRepository.findOne(id);
		if(project.getRuns() != null && project.getRuns().size() > 0){
			redirectAttrs.addFlashAttribute("error","Unable to delete this project because it's attached to at least one run");
		}
        else if(project.getScripts() != null && project.getScripts().size() > 0){
            redirectAttrs.addFlashAttribute("error","Unable to delete this project because it's attached to at least one script");
        }
        else{
            projectRepository.delete(id);
		}
        return "redirect:/project";
    }    
	
	/**
	 * Update a project (name or description)
	 * @param projectId	ID of the project to update
	 * @param name	New name
	 * @param description	New description
	 * @return	
	 */
    @RequestMapping(value = "/project/{projectId}", method = RequestMethod.POST)
    @ResponseBody
    public String updateProject(
                            @PathVariable("projectId") Long projectId,
                            @RequestParam(value="name",required=false) String name,
                            @RequestParam(value="description",required=false) String description
                            ) {
        Project project = projectRepository.findOne(projectId);
        String valueToReturn = name;
        if(name != null){
        	project.setName(name);
        }
        else if(description != null){
        	project.setDescription(description);
        	valueToReturn = description;
        }
        projectRepository.save(project);
        return valueToReturn;
    }  
   
}
