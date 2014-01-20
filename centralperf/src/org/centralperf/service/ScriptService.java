package org.centralperf.service;

import java.util.List;

import javax.annotation.Resource;

import org.centralperf.helper.JMXScriptVariableExtractor;
import org.centralperf.model.dao.Project;
import org.centralperf.model.dao.Script;
import org.centralperf.model.dao.ScriptVariableSet;
import org.centralperf.model.dao.ScriptVersion;
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
		List<ScriptVariableSet> scriptVariableSets = JMXScriptVariableExtractor.extractVariables(script.getVersions().get(0).getJmx());
		script.getVersions().get(0).setScriptVariableSets(scriptVariableSets);
		scriptRepository.save(script);	
	}
	
	// Add a new script to the repository
	public Script addScript(Project project, String label, String description, String jmxContent){
		Script script = new Script();
		script.setLabel(label);
		script.setDescription(description);
		script.setProject(project);
		ScriptVersion scriptVersion = new ScriptVersion();
		scriptVersion.setDescription("First version");
		scriptVersion.setNumber(1L);
		scriptVersion.setJmx(jmxContent);
        script.getVersions().add(0, scriptVersion);
        
        addScript(script);
        
        return script;
	}	
	
}
