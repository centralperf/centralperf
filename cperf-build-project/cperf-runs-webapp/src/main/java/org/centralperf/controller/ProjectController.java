package org.centralperf.controller;

import org.centralperf.model.Project;
import org.centralperf.model.Run;
import org.centralperf.model.Script;
import org.centralperf.repository.ProjectRepository;
import org.centralperf.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;

@Controller
public class ProjectController {
	
	private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    @Resource
    private ProjectService projectService;

    @Resource
    private ProjectRepository projectRepository;


    @RequestMapping(value = "/project/new", method = RequestMethod.GET)
    public String createProject(Model model) {
        model.addAttribute("newProject",new Project());
        return "projectCreate";
    }

	@RequestMapping(value = "/project/new", method = RequestMethod.POST)
    public String addProject(
    						@ModelAttribute("project") Project project) {
        projectService.addProject(project);
        return "redirect:/project/" + project.getId() + "/detail";
    }
     
    @RequestMapping("/project")
    public String showProjects(Model model) {
    	model.addAttribute("projects",projectRepository.findAll());
    	model.addAttribute("newProject",new Project());
        return "projects";
    }
    
    @RequestMapping(value="/project/{id}/detail", method = RequestMethod.GET)
    public String showScriptDetail(@PathVariable("id") Long id, Model model){
    	log.debug("project details for project ["+id+"]");
        Project project = projectRepository.findOne(id);
    	model.addAttribute("project",project);
        model.addAttribute("runs",projectService.getLastRuns(project));
        model.addAttribute("scripts",project.getScripts());
        model.addAttribute("newScript",new Script());
        model.addAttribute("newRun",new Run());
        return "projectDetail";
    }    
    
	@RequestMapping(value = "/project/{id}/delete", method = RequestMethod.GET)
    public String deleteScript(@PathVariable("id") Long id, RedirectAttributes redirectAttrs) {
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
   
}
