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

import org.centralperf.model.dao.Project;
import org.centralperf.model.dao.Run;
import org.centralperf.repository.ProjectRepository;
import org.centralperf.repository.RunRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Manage operations on projects
 *
 * @since 1.0
 */
@Service
public class ProjectService {

	@Resource
	private ProjectRepository projectRepository;

    @Resource
    private RunRepository runRepository;
	
    /**
     * Add a new project
     * @param project
     */
	public void addProject(Project project){
        projectRepository.save(project);
	}

	/**
	 * Get last runs for a project. In fact, new runs first
	 * @param project
	 * @return A list of date reverse ordered runs for this project 
	 */
    public List<Run> getLastRuns(Project project) {
		return runRepository.findByProjectIdOrderByLastStartDateDesc(project.getId());
	}
	
}
