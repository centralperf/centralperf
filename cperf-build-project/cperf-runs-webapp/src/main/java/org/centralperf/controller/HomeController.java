package org.centralperf.controller;

import org.centralperf.repository.ProjectRepository;
import org.centralperf.repository.RunRepository;
import org.centralperf.service.RunService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @RequestMapping(value = "/")
	public String home(Model model) {
		return "redirect:/home";
	}
	
	@RequestMapping(value = "/home")
	public String test(Model model) {
        model.addAttribute("runs",runService.getLastRuns());
        model.addAttribute("activeRuns",runService.getActiveRuns());
        model.addAttribute("projects",projectRepository.findAll());
		return "home";
	}

}
