package org.centralperf.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

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

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Run> getRuns() {
        return runs;
    }

    public void setRuns(List<Run> runs) {
        this.runs = runs;
    }

    public List<Script> getScripts() {
        return scripts;
    }

    public void setScripts(List<Script> scripts) {
        this.scripts = scripts;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
       
}
