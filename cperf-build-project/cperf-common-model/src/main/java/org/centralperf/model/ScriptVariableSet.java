package org.centralperf.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * A set of variable for a script
 * @author Charles Le Gallic
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
