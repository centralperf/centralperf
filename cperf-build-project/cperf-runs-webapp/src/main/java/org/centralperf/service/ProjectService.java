package org.centralperf.service;

import org.centralperf.helper.JMXScriptVariableExtractor;
import org.centralperf.model.Project;
import org.centralperf.model.Script;
import org.centralperf.model.ScriptVariableSet;
import org.centralperf.repository.ProjectRepository;
import org.centralperf.repository.ScriptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Manage operations on projects
 * @author Charles Le Gallic
 *
 */
@Service
public class ProjectService {

	@Resource
	private ProjectRepository projectRepository;
	
	private static final Logger log = LoggerFactory.getLogger(ProjectService.class);
	
	// Add a new project to the repository
	public void addProject(Project project){
        projectRepository.save(project);
	}
	
}
