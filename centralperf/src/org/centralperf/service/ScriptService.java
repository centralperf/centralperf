package org.centralperf.service;

import java.util.List;

import javax.annotation.Resource;

import org.centralperf.model.dao.Project;
import org.centralperf.model.dao.Script;
import org.centralperf.model.dao.ScriptVariableSet;
import org.centralperf.model.dao.ScriptVersion;
import org.centralperf.repository.ScriptRepository;
import org.centralperf.repository.ScriptVersionRepository;
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

	@Resource
	private ScriptVersionRepository scriptVersionRepository;	
	
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
	
	// Add a new script version to the repository
	public void addScriptVersion(ScriptVersion scriptVersion){
		
		// get the associated sampler
		Sampler sampler = samplerService.getSamplerByUID(scriptVersion.getScript().getSamplerUID());
		
		// Extract variables from Script file
		List<ScriptVariableSet> scriptVariableSets = sampler.getScriptProcessor().getVariableSets(scriptVersion.getContent());
		if(scriptVariableSets != null)
			scriptVersion.setScriptVariableSets(scriptVariableSets);
		
		// Saving script version to repository
		scriptVersionRepository.save(scriptVersion);	
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
	
	// Add a new script version to the repository
	public ScriptVersion addScriptVersion(Script script, String description, String content){
		ScriptVersion scriptVersion = new ScriptVersion();
		scriptVersion.setDescription(description);
		scriptVersion.setContent(content);
		List<ScriptVersion> scriptVersions = script.getVersions();
		long maxScriptVersion = -1;
		for(ScriptVersion version:scriptVersions){
			if(maxScriptVersion < version.getNumber()) maxScriptVersion = version.getNumber() + 1;
		}
		scriptVersion.setNumber(maxScriptVersion);
		scriptVersion.setScript(script);
        script.getVersions().add(0, scriptVersion);        
        addScriptVersion(scriptVersion);
        return scriptVersion;
	}	
	
	public void deleteScriptVersion(long scriptVersionId){
		ScriptVersion scriptVersion = scriptVersionRepository.findOne(scriptVersionId);
		Script script = scriptVersion.getScript();
		for(ScriptVersion version:script.getVersions()){
			if(version.getId() == scriptVersionId){
				script.getVersions().remove(version);
				break;
			}
		}
		scriptRepository.save(script);
	}
	
	
	
}
