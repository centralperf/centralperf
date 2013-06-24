package org.centralperf.service;

import java.util.List;

import javax.annotation.Resource;

import org.centralperf.helper.JMXScriptVariableHelper;
import org.centralperf.model.Script;
import org.centralperf.model.ScriptVariableSet;
import org.centralperf.repository.ScriptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Manage operations on scripts
 * @author Charles Le Gallic
 *
 */
@Service
public class ScriptService {

	@Resource
	private ScriptRepository scriptRepository;
	
	private static final Logger log = LoggerFactory.getLogger(ScriptService.class);
	
	// Add a new script to the repository
	public void addScript(Script script){
		
		// Extract variables from JMX file
		List<ScriptVariableSet> scriptVariableSets = JMXScriptVariableHelper.extractVariables(script.jmx);
		script.setScriptVariableSets(scriptVariableSets);
		scriptRepository.save(script);	
	}
	
}
