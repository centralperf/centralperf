package org.centralperf.controller;

import javax.annotation.Resource;

import org.centralperf.service.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ConfigurationController {
	
	private static final Logger log = LoggerFactory.getLogger(ConfigurationController.class);

    @Resource
    private ConfigurationService configurationService;
	
    @RequestMapping(value="/configuration",method=RequestMethod.GET)
    public String showConfiguration(Model model) {
    	return showConfigurationWithGroup(-1, model);
    }
    
    @RequestMapping(value="/configuration/{group}",method=RequestMethod.GET)
    public String showConfigurationWithGroup(
    		@PathVariable("group") int group,
    		Model model) {
    	model.addAttribute("config", configurationService);
    	model.addAttribute("group",group);
    	return "configuration";
    }
    
    @RequestMapping(value="/configuration/update",method=RequestMethod.POST)
    @ResponseBody
    public boolean updateRunVariables(
    		@RequestParam("keyName") String keyName,
    		@RequestParam("keyValue") String keyValue,
    		Model model){
    	log.warn("Update key " + keyName + " with value " +  keyValue);
    	configurationService.updateConfigurationValue(keyName, keyValue);
    	return true;
    }  
 
    /*
     * WARN: Do not delete /{group} as keyname use . and throw in folowing spring bugs
     *  - https://jira.spring.io/browse/SPR-6164
     *  - https://jira.spring.io/browse/SPR-7632
     */
    @RequestMapping(value="/configuration/revert/{keyName}/{group}",method=RequestMethod.GET)
    public String updateRunVariables(
    		@PathVariable("keyName") String keyName,
    		@PathVariable("group") int group,
    		Model model){
    	log.warn("revert was asked for key " + keyName);
    	configurationService.deleteConfigurationValue(keyName);
    	return "redirect:/configuration/"+group;
    } 
    
}
