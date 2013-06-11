package org.centralperf.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class Script{

	@Id
	@GeneratedValue
	public Long id;

	public String label;
	
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

}
