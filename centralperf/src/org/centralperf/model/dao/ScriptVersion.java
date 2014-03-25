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

import org.hibernate.annotations.Type;

import java.util.List;

/**
 * A version of a script is the content of the script with a version number and a description
 * @since 1.0
 */
@Entity
public class ScriptVersion {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Number of the version
     */
    @OrderColumn
    private Long number;

    /**
     * Short description for this version
     */
    private String description;

    /**
     * Set of variables for this script
     */
    @OneToMany(fetch = FetchType.EAGER, cascade= CascadeType.ALL)
    private List<ScriptVariableSet> scriptVariableSets;

    @Lob
    @Column( length = 100000000 )
    @Type(type="text")
    public String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scriptId")
    private Script script;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ScriptVariableSet> getScriptVariableSets() {
        return scriptVariableSets;
    }

    public void setScriptVariableSets(List<ScriptVariableSet> scriptVariableSets) {
        this.scriptVariableSets = scriptVariableSets;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public Script getScript() {
		return script;
	}
    
    public void setScript(Script script) {
		this.script = script;
	}
}
