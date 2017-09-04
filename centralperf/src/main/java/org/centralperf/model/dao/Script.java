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
import java.util.List;

import javax.persistence.*;

/**
 * A script is a test definition. A script has at least a scriptVersion and is attached to a project.<br/>
 * A script currently cannot belong to several projects.<br/>
 * The kind of a script (jMeter, Gatling...) is determined by his SamplerUID. Each SamplerUID is unique for a Sampler(injector)<br/>
 * It's also an Entity bean to be persisted 
 * @since 1.0
 */
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
