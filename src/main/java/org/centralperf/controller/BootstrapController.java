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

import javax.annotation.Resource;

import org.centralperf.service.BootstrapService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The Bootstrap controller is used when CP is launched for the first time, to propose to initialize with samples
 * 
 * @since 1.0
 * 
 */
@Controller
public class BootstrapController {
	
	@Resource
	BootstrapService bootstrapService;
	
	/**
	 * Initial bootstrap page
	 * @return
	 */
	@GetMapping(value = "/bootstrap")
	public String showBootstrapPage() {
		return "bootstrap";
	}
	
	/**
	 * Import samples or not
	 * @param importSamples True if the user has choosen to import the samples
	 * @return Path to the home page mapping
	 */
	@GetMapping(value = "/bootstrap/initialize")
	public String initialize(@RequestParam(value="importSamples") Boolean importSamples) {
		// Import CP samples if specified 
		if(importSamples){
			bootstrapService.importSamples();
		}
		
		// Update configuration, set as initialized
		bootstrapService.setInitialized();
		
		// Display Welcome Page
		return "redirect:/home";
	}	

}
