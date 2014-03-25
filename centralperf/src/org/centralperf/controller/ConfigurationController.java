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

/**
 * The Configuration controller is used to allow en user interaction with CP configuration through the Web interface
 * @since 1.0
 */
@Controller
public class ConfigurationController {
	
	private static final Logger log = LoggerFactory.getLogger(ConfigurationController.class);

    @Resource
    private ConfigurationService configurationService;
	
    /**
     * Shows all configuration
     * @param model Model prepared for the configuration page view
     * @return name of the main configuration view
     */
    @RequestMapping(value="/configuration",method=RequestMethod.GET)
    public String showConfiguration(Model model) {
    	return showConfigurationWithGroup(-1, model);
    }
    
    /**
     * Shows configuration for a specific group
     * @param group Group to display
     * @param model Model prepared for the configuration page view
     * @return name of the main configuration view
     */    
    @RequestMapping(value="/configuration/{group}",method=RequestMethod.GET)
    public String showConfigurationWithGroup(
    		@PathVariable("group") int group,
    		Model model) {
    	model.addAttribute("config", configurationService);
    	model.addAttribute("group",group);
    	return "configuration";
    }
    
    /**
     * Update a configuration value
     * @param keyName	Key of the configuration element to update
     * @param keyValue	New value
     * @param model
     * @return
     */
    @RequestMapping(value="/configuration/update",method=RequestMethod.POST)
    @ResponseBody
    public boolean updateRunVariables(
    		@RequestParam("keyName") String keyName,
    		@RequestParam("keyValue") String keyValue){
    	log.warn("Update key " + keyName + " with value " +  keyValue);
    	configurationService.updateConfigurationValue(keyName, keyValue);
    	return true;
    }  
 
    /*
     * WARN: Do not delete /{group} as keyname use . and throw in folowing spring bugs
     *  - https://jira.spring.io/browse/SPR-6164
     *  - https://jira.spring.io/browse/SPR-7632
     */
    /**
     * Revert to the default configuration
     * @param keyName	Key for the configuration element to revert
     * @param group Group of this key
     * @return	A redirection to display current group
     */
    @RequestMapping(value="/configuration/revert/{keyName}/{group}",method=RequestMethod.GET)
    public String updateRunVariables(
    		@PathVariable("keyName") String keyName,
    		@PathVariable("group") int group){
    	log.warn("revert was asked for key " + keyName);
    	configurationService.deleteConfigurationValue(keyName);
    	return "redirect:/configuration/"+group;
    } 
    
}
