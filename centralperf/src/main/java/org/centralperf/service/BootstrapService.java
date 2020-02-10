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

package org.centralperf.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import javax.annotation.Resource;

import org.centralperf.model.Configuration;
import org.centralperf.model.dao.Project;
import org.centralperf.model.dao.Run;
import org.centralperf.repository.RunRepository;
import org.centralperf.sampler.driver.gatling.GatlingSampler;
import org.centralperf.sampler.driver.jmeter.JMeterSampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Allows to create sample data to initialize CentralPerf
 * @since 1.0
 *
 */
@Service
public class BootstrapService implements InitializingBean  {

	@Resource
	private ConfigurationService configurationService;

	@Resource
	private ScriptService scriptService;
	
	@Resource
	private ProjectService projectService;

	@Resource
	private BootstrapServiceFiles bootstrapServiceFiles;
	
	@Resource
	private RunRepository runRepository;
	
	private static final Logger log = LoggerFactory.getLogger(BootstrapService.class);

	/**
	 * Check if it's necessary to launch bootstrap.
	 * If the user has already imported sample data or refused it, then it returns false 
	 * @return true if already initialized, false otherwise
	 */
	public boolean isAlreadyInitialized(){
		Boolean initialized = Boolean.parseBoolean(configurationService.getConfigurationValue(Configuration.INITIALIZED));
		return initialized != null ? initialized : false;
	}
	
	public void setInitialized(){
		// Update configuration, set as initialized
		configurationService.updateConfigurationValue(Configuration.INITIALIZED, Boolean.TRUE.toString());
	}
	
	public void importSamples(){
		
		// Create sample Projet
		Project sampleProject = new Project();
		sampleProject.setName("Sample project");
		sampleProject.setDescription("Central Perf default sample Project");
		projectService.addProject(sampleProject);		
		
		// Associate sample script
		// Load sample JMX and Gatling files
		String jmxContent;
		//String gatlingContent;
		try {
			jmxContent = new Scanner(bootstrapServiceFiles.getSampleJMXFile().getFile()).useDelimiter("\\Z").next();
			scriptService.addScript(sampleProject,JMeterSampler.UID, "JMETER Sample script", "Central Perf sample JMETER script. Queries a single URL with few scenario's parameters", jmxContent);
			//gatlingContent = new Scanner(bootstrapServiceFiles.getSampleGatlingFile().getFile()).useDelimiter("\\Z").next();
			//scriptService.addScript(sampleProject,GatlingSampler.UID, "GATLING Sample script", "Central Perf sample script. Queries Google; only for demonstration (no parameters)", gatlingContent);
		} catch (FileNotFoundException e) {
			log.error("Error on bootstrap import:"+e.getMessage(), e);
		} catch (IOException e) {
			log.error("IO Error on bootstrap import:"+e.getMessage(), e);
		}
		
		// Import sample resuts
		// TODO : Import sample result
	}

	/**
	 * Check if jobs where running while the application stopped (after a crash for example ...)
	 * These runs should not be running on startup
	 */
	private void fixIncompleteRuns(){
		List<Run> incompleteRuns = runRepository.findByRunning(true);
		for (Run incompleteRun : incompleteRuns) {
			incompleteRun.setRunning(false);
			incompleteRun.setComment(
					(incompleteRun.getComment()!=null ? incompleteRun.getComment() : "") + 
					"\r\n*** System message : this run didn't complete normally *** ");
			runRepository.save(incompleteRun);
		}
	}
	
	/**
	 * Launched after container started
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		log.debug("Launch bootstrap actions");
		
		log.debug("fix uncomplete runs");
		fixIncompleteRuns();
	}
	
}
