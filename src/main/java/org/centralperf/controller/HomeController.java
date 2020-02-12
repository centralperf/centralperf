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

/**
 * The Home controller manage the display of CP home page
 *  
 * @since 1.0
 * 
 */
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
    
    /**
     * Home page of CP
     * @param model	Model prepared for the home page view
     * @return Name of the home page view
     */
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
