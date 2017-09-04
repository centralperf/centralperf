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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.centralperf.model.SampleDataBackendTypeEnum;
import org.hibernate.annotations.Type;


/**
 * A run is based on a script (specific version) associated with custom variable values (number of users, duration....)
 * A run may ready to be launched, running or achieved. An already launched run cannot be launched again (in fact you can, but a new run is then created)  
 * This is an Entity bean to persist Run info into the persistence layer.
 * 
 * @since 1.0
 */
@Entity
public class Run {

	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	@Size(min = 1, max = 33)
	private String label;

	@ManyToOne
	private ScriptVersion scriptVersion;
	
	private boolean launched = false;
	
	private boolean running = false;
	
	private Date startDate;
	
	private Date endDate;
	
	private String comment;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<ScriptVariable> customScriptVariables = new ArrayList<ScriptVariable>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private Project project;
	
	@Lob
	@Column( length = 1000000 )
	@Type(type="text")
	private String processOutput;
	
	@OneToMany(mappedBy="run", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Sample> samples;
	
	private SampleDataBackendTypeEnum sampleDataBackendType = SampleDataBackendTypeEnum.DEFAULT;
	
	/**
	 * @return true if the run has been launched. It may be running or not.
	 */
	public boolean isLaunched() {
		return launched;
	}

	/**
	 * Set to true to indicate a Run has been launched
	 * @param launched	if the run has been launched
	 */
	public void setLaunched(boolean launched) {
		this.launched = launched;
	}

	/**
	 * @return true if the run is currently running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @param running If true, then the run is currently running	
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	public Long getId() {
		return id;
	}

	/**
	 * Set the unique key used by JPA for persistence (automatically generated)
	 * @param id	Unique key
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	/**
	 * Set the label that will be displayed in UI for this run
	 * @param label	label of the run
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	public ScriptVersion getScriptVersion() {
		return scriptVersion;
	}

	/**
	 * A run is associated to a specific version of a script. Specific variables values for a run are linked to the script version
	 * @param scriptVersion	Version of the script for this run
	 */
	public void setScriptVersion(ScriptVersion scriptVersion) {
		this.scriptVersion = scriptVersion;
	}

	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Set the date/time the run has been launched
	 * @param startDate	Date the run has been launched
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	/**
	 * The date the run end (automatically or aborted)
	 * @param endDate	Date the run has ended
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getProcessOutput() {
		return processOutput;
	}

	/**
	 * Set the output of the injector (jMeter or Gatling for example). May be useful to know the status of underlaying injectors
	 * @param processOutput	Raw output of injectors
	 */
	public void setProcessOutput(String processOutput) {
		this.processOutput = processOutput;
	}
	
	public List<Sample> getSamples() {
		return samples;
	}

	/**
	 * A list of all samples generated during the Run.
	 * @param samples	A list of samples
	 */
	public void setSamples(List<Sample> samples) {
		this.samples = samples;
	}

	public List<ScriptVariable> getCustomScriptVariables() {
		return customScriptVariables;
	}

	/**
	 * Script variable that have been updated for this run. CP only stores custom variable for a Run. Other values will be taken from the default value for a variable
	 * @param customScriptVariables	List of custom variables
	 */
	public void setCustomScriptVariables(List<ScriptVariable> customScriptVariables) {
		this.customScriptVariables = customScriptVariables;
	}

    public Project getProject() {
        return project;
    }

    /**
     * Set the project a Run belongs to. A run cannot belong to multiple projects
     * @param project	The parent project
     */
    public void setProject(Project project) {
        this.project = project;
    }

	public String getComment() {
		return comment;
	}

	/**
	 * Set the "free text" comment added by the user to describe platforms, environment, circumstances... of the run
	 * @param comment	Free text for comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Type of backend used to store this run sample results data
	 * @return
	 */
	public SampleDataBackendTypeEnum getSampleDataBackendType() {
		return sampleDataBackendType;
	}

	public void setSampleDataBackendType(SampleDataBackendTypeEnum sampleDataStorageTypeEnum) {
		this.sampleDataBackendType = sampleDataStorageTypeEnum;
	}
}
