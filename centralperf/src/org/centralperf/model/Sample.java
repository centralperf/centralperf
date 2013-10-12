package org.centralperf.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Sample implements Serializable, Comparable<Sample>{

	private static final long serialVersionUID = 1758196304097304496L;

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="run_fk")
	private Run run;
	
	private Date timestamp;
	private long elapsed;
	private String sampleName;
	private long returnCode;
	private long latency;
	private long sizeInOctet;
	private boolean assertResult;
	private int status;
	
	public Run getRun() {
		return run;
	}
	public void setRun(Run run) {
		this.run = run;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public long getElapsed() {
		return elapsed;
	}
	public void setElapsed(long elapsed) {
		this.elapsed = elapsed;
	}
	public String getSampleName() {
		return sampleName;
	}
	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}
	public long getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(long returnCode) {
		this.returnCode = returnCode;
	}
	public long getLatency() {
		return latency;
	}
	public void setLatency(long latency) {
		this.latency = latency;
	}
	public long getSizeInOctet() {
		return sizeInOctet;
	}
	public void setSizeInOctet(long sizeInOctet) {
		this.sizeInOctet = sizeInOctet;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isAssertResult() {
		return assertResult;
	}
	public void setAssertResult(boolean assertResult) {
		this.assertResult = assertResult;
	}
	
	@Override
	public String toString() {
		return "org.centralperf.model.Sample ["+id+"]["+run.getId()+"]["+timestamp+"]["+elapsed+"]["+sampleName+"]["+returnCode+"]["+latency+"]["+sizeInOctet+"]["+assertResult+"]["+status+"]";
	}
	
	@Override
	public int compareTo(Sample s) {
		//Sort by ID (default)
		return this.id.compareTo(s.getId());
	}
}
