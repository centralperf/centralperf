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
package org.centralperf.model.dao;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

/**
 * Project are top level entities in CP. Runs and Script are attached to projects.
 * 
 * @since 1.0
 * 
 */
@Entity
public class Project {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 1, max = 33)
    private String name;

    @JsonIgnore
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "project")
    private List<Run> runs;

    @JsonIgnore
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "project")
    private List<Script> scripts;
   
    private String description;

    public Long getId() {
        return id;
    }

	/**
	 * Unique key used by JPA for persistence (automatically generated)
	 * @param id	Unique key
	 */
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    /**
     * Set the name of the project, displayed on the UI
     * @param name Name of the project
     */
    public void setName(String name) {
        this.name = name;
    }

    public List<Run> getRuns() {
        return runs;
    }

    /**
     * Set all the runs for this project
     * @param runs A list of runs
     */
    public void setRuns(List<Run> runs) {
        this.runs = runs;
    }

    public List<Script> getScripts() {
        return scripts;
    }

    /**
     * Set all the scripts for this project
     * @param runs A list of scripts
     */
    public void setScripts(List<Script> scripts) {
        this.scripts = scripts;
    }

	public String getDescription() {
		return description;
	}

    /**
     * Set project description
     * @param description A description of this project 
     */
	public void setDescription(String description) {
		this.description = description;
	}
       
}
