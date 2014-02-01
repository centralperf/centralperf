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
import org.centralperf.sampler.driver.jmeter.JMeterSampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * Allows to create sample data to initialize CentralPerf
 * @author Charles Le Gallic
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

	@Resource(name="bootstrapFiles")
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
		Boolean initialized = configurationService.getConfigurationValueAsBoolean(Configuration.INITIALIZED);
		return initialized != null ? initialized : false;
	}
	
	public void setInitialized(){
		// Update configuration, set as initialized
		configurationService.updateConfigurationValue(Configuration.INITIALIZED, Boolean.TRUE);
	}
	
	public void importSamples(){
		
		// Create sample Projet
		Project sampleProject = new Project();
		sampleProject.setName("Sample project");
		sampleProject.setDescription("Central Perf default sample Project");
		projectService.addProject(sampleProject);		
		
		// Associate sample script
		// Load sample JMX
		String jmxContent;
		try {
			jmxContent = new Scanner(bootstrapServiceFiles.getSampleJMXFile().getFile()).useDelimiter("\\Z").next();
			scriptService.addScript(sampleProject,JMeterSampler.UID, "Sample script", "Central Perf sample script. Queries a single URL with few scenario's parameters", jmxContent);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
