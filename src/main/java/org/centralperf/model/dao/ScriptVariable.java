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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Script variable are extracted by the samplers drivers from script files. It allows for the end user to launch run with specific variable values
 * @since 1.0
 */
@Entity
public class ScriptVariable {

	@Id
	@GeneratedValue
	private Long id;
	
	private String name;
	private String defaultValue;
	private String value;
	private String description;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public ScriptVariable clone() {
		ScriptVariable scriptVariable = new ScriptVariable();
		scriptVariable.setName(this.getName());
		scriptVariable.setValue(this.getValue());
		scriptVariable.setDescription(this.getDescription());
		scriptVariable.setDefaultValue(this.getDefaultValue());
		return scriptVariable;
	};
	
	@Override
	public String toString() {
		return "ScriptVariable. Name = " + this.getName() 
				+ ", value = " + this.getValue()				
				+ ", default value = " + this.getDefaultValue() 
				+ ", description = " + this.getDescription();
	}
	
}
