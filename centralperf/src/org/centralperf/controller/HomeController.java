package org.centralperf.controller;

import org.centralperf.model.dao.Project;
import org.centralperf.model.dao.Run;
import org.centralperf.repository.ProjectRepository;
import org.centralperf.repository.RunRepository;
import org.centralperf.service.BootstrapService;
import org.centralperf.service.RunService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
public class HomeController {

    @Resource
    private RunRepository runRepository;

    @Resource
    private RunService runService;

    @Resource
    private ProjectRepository projectRepository;

    @Resource
    private BootstrapService bootstrapService;    
    	
	@RequestMapping(value = {"/home","/"})
	public String home(Model model) {
		// Check if Central Perf has already been initialized
		if(!bootstrapService.isAlreadyInitialized()){
			return "redirect:bootstrap";
		}
        model.addAttribute("runs",runService.getLastRuns());
        model.addAttribute("activeRuns",runService.getActiveRuns());
        model.addAttribute("projects",projectRepository.findAll());
        model.addAttribute("newRun",new Run());
        model.addAttribute("newProject",new Project());
		return "home";
	}

}
