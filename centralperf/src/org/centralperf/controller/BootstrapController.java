package org.centralperf.controller;

import javax.annotation.Resource;

import org.centralperf.service.BootstrapService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BootstrapController {
	
	@Resource
	BootstrapService bootstrapService;
	
	@RequestMapping(value = "/bootstrap")
	public String showBootstrapPage() {
		return "bootstrap";
	}
	
	@RequestMapping(value = "/bootstrap/initialize")
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
