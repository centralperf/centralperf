package org.centralperf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping(value = "/")
	public String home(Model model) {
		return "redirect:run";
	}
	
	@RequestMapping(value = "/test")
	public String test(Model model) {
		return "test";
	}

}
