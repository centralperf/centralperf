package org.centralperf.model.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;

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
	
	public boolean isLaunched() {
		return launched;
	}

	public void setLaunched(boolean launched) {
		this.launched = launched;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ScriptVersion getScriptVersion() {
		return scriptVersion;
	}

	public void setScriptVersion(ScriptVersion scriptVersion) {
		this.scriptVersion = scriptVersion;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getProcessOutput() {
		return processOutput;
	}

	public void setProcessOutput(String processOutput) {
		this.processOutput = processOutput;
	}
	
	public List<Sample> getSamples() {
		return samples;
	}

	public void setSamples(List<Sample> samples) {
		this.samples = samples;
	}

	public List<ScriptVariable> getCustomScriptVariables() {
		return customScriptVariables;
	}

	public void setCustomScriptVariables(List<ScriptVariable> customScriptVariables) {
		this.customScriptVariables = customScriptVariables;
	}

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
    
    
}