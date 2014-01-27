package org.centralperf.service;

import java.util.List;

import javax.annotation.Resource;

import org.centralperf.model.dao.Project;
import org.centralperf.model.dao.Script;
import org.centralperf.model.dao.ScriptVariableSet;
import org.centralperf.model.dao.ScriptVersion;
import org.centralperf.repository.ScriptRepository;
import org.centralperf.sampler.api.Sampler;
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
	private SamplerService samplerService;
	
	@Resource
	private ScriptRepository scriptRepository;
	
	private static final Logger log = LoggerFactory.getLogger(ScriptService.class);
	
	// Add a new script to the repository
	public void addScript(Script script){
		
		// get the associated sampler
		Sampler sampler = samplerService.getSamplerByUID(script.getSamplerUID());
		
		// Extract variables from Script file
		List<ScriptVariableSet> scriptVariableSets = sampler.getScriptProcessor().getVariableSets(script.getVersions().get(0).getContent());
		if(scriptVariableSets != null)
			script.getVersions().get(0).setScriptVariableSets(scriptVariableSets);
		
		// Saving script to repository
		scriptRepository.save(script);	
	}
	
	// Add a new script to the repository
	public Script addScript(Project project, String samplerUID, String label, String description, String content){
		Script script = new Script();
		script.setLabel(label);
		script.setSamplerUID(samplerUID);
		script.setDescription(description);
		script.setProject(project);
		ScriptVersion scriptVersion = new ScriptVersion();
		scriptVersion.setDescription("First version");
		scriptVersion.setNumber(1L);
		scriptVersion.setContent(content);
		scriptVersion.setScript(script);
        script.getVersions().add(0, scriptVersion);
        
        addScript(script);
        
        return script;
	}	
	
}
