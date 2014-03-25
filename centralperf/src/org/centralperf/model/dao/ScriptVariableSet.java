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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Variable sets are functionnal groups of variable extracted from a script
 * @since 1.0
 */
@Entity
public class ScriptVariableSet {

	@Id
	@GeneratedValue
	private Long id;
	
	private String name;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<ScriptVariable> scriptVariables = new ArrayList<ScriptVariable>();
	
	public List<ScriptVariable> getScriptVariables() {
		return scriptVariables;
	}
	public void setScriptVariables(List<ScriptVariable> scriptsVariable) {
		this.scriptVariables = scriptsVariable;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Script variable set. Name = " + this.getName() + "\r\n");
		builder.append("==================\r\n");
		for (ScriptVariable scriptsVariable : this.getScriptVariables()) {
			builder.append(scriptsVariable.toString() + "\r\n");
		}
		builder.append("==================\r\n");
		return builder.toString();
	}
	
}
