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

package org.centralperf.repository;

import java.util.List;

import org.centralperf.model.dao.Run;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Runs Spring Data (http://projects.spring.io/spring-data-jpa/) based repository
 * @since 1.0
 */
public interface RunRepository extends PagingAndSortingRepository<Run, Long> {
	/**
	 * Get all run associated with a specific Script
	 * @param id	Id of the script
	 * @return	A list of runs associated with this script
	 */
	List<Run> findByScriptVersionScriptId(Long id);
	
	/**
	 * Get all run associated with a specific ScriptVersion
	 * @param id	Id of the script version
	 * @return	A list of runs associated with this script version
	 */
	List<Run> findByScriptVersionId(Long id);
	
	/**
	 * Get all run for a project ordered by start date (reverse)
	 * @param id	Id of the project
	 * @return	A list of runs associated with this project
	 */	
    List<Run> findByProjectIdOrderByStartDateDesc(Long id);
    
	/**
	 * Get all run currently running
	 * @param running	Running or not
	 * @return	A list of runs on current running state
	 */	    
    List<Run> findByRunning(boolean running);
}
