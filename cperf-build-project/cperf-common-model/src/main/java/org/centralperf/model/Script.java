package org.centralperf.model;

import java.util.List;

import javax.persistence.*;

@Entity
public class Script{

	@Id
	@GeneratedValue
	private Long id;

	/**
	 * Label of the script
	 */
	private String label;

    /**
     * Short description for the script
     */
    private String description;

	/**
	 * Set of variables for this script 
	 */
	@OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	private List<ScriptVariableSet> scriptVariableSets;
	
	@Lob
	@Column( length = 100000 )
	public String jmx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private Project project;

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

	public String getJmx() {
		return jmx;
	}

	public void setJmx(String jmx) {
		this.jmx = jmx;
	}

	public List<ScriptVariableSet> getScriptVariableSets() {
		return scriptVariableSets;
	}

	public void setScriptVariableSets(List<ScriptVariableSet> scriptVariableSets) {
		this.scriptVariableSets = scriptVariableSets;
	}

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
