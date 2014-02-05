package org.centralperf.model.dao;

import java.util.ArrayList;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private Project project;

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
    @OrderColumn(name="INDEX")
    private List<ScriptVersion> versions = new ArrayList<ScriptVersion>();

    /**
     * Reference to the kind of sampler associated to this script
     */
    private String samplerUID;
    
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

    public List<ScriptVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<ScriptVersion> versions) {
        this.versions = versions;
    }

	public String getSamplerUID() {
		return samplerUID;
	}

	public void setSamplerUID(String samplerUID) {
		this.samplerUID = samplerUID;
	}
}
