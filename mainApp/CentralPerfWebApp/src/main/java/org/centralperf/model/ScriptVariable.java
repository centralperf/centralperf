package org.centralperf.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
	public String toString() {
		return "ScriptVariable. Name = " + this.getName() 
				+ ", value = " + this.getValue()				
				+ ", default value = " + this.getDefaultValue() 
				+ ", description = " + this.getDescription();
	}
	
}
