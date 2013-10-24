package org.centralperf.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

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
	 * Set of variables for this script 
	 */
	@OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	private List<ScriptVariableSet> scriptVariableSets;
	
	@Lob
	@Column( length = 100000 )
	public String jmx;

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

}
