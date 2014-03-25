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

import org.centralperf.model.dao.ScriptVersion;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Script version Spring Data (http://projects.spring.io/spring-data-jpa/) based repository
 * @since 1.0
 */
public interface ScriptVersionRepository extends PagingAndSortingRepository<ScriptVersion, Long> {
	
	/**
	 * Get all script versions of a script, order by their number (new versions first) 
	 * @param id	Id of the script
	 * @return	All script versions for this run, new versions first
	 */
	List<ScriptVersion> findByScriptIdOrderByNumberDesc(Long id);
}
