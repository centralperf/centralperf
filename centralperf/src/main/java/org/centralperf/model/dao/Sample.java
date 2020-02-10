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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

/**
 * A Sample is all informations gathered by an injector for each single request during a run
 * The bean is also an Entity for the persistance layer
 * @since 1.0
 */
@Entity
public class Sample implements Serializable, Comparable<Sample>{

	private static final long serialVersionUID = 1758196304097304496L;

	@Id
    @SequenceGenerator(name = "sample_seq_gen", sequenceName = "sample_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sample_seq_gen")
	private Long id;
	
	@JsonIgnore
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
	private long grpThreads;
	private long allThreads;
	private String status;
	
	public Run getRun() {
		return run;
	}
	/**
	 * The run this sample if generated from
	 * @param run The run this sample if generated from	
	 */
	public void setRun(Run run) {
		this.run = run;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	/**
	 * Date/time the request has been launched 
	 * @param timestamp
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public long getElapsed() {
		return elapsed;
	}
	/**
	 * Generally, the time elapsed before the beginning of the request and the after the end of a request in milliseconds.<br/>
	 * <a href="https://jmeter.apache.org/usermanual/glossary.html">For jMeter</a> (from jMeter documentation : <i>"JMeter measures the elapsed time from just before sending the request to just after the last response has been received. 
	 * JMeter does not include the time needed to render the response, nor does JMeter process any client code, for example Javascript."</i>)
	 * 
	 * @param elapsed
	 */
	public void setElapsed(long elapsed) {
		this.elapsed = elapsed;
	}
	public String getSampleName() {
		return sampleName;
	}
	/**
	 * Name of the sample. Generally, set by the name of the sample in the script source file
	 * @param sampleName
	 */
	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}
	public long getReturnCode() {
		return returnCode;
	}
	/**
	 * The code returned by the sample (200, 404 or 500 for examples for HTTP requests)
	 * @param returnCode
	 */
	public void setReturnCode(long returnCode) {
		this.returnCode = returnCode;
	}
	
	public long getLatency() {
		return latency;
	}
	/**
	 * Generally, the time taken to launch the request, and by the server to compute it and send the first response, in miliseconds
	 * <a href="https://jmeter.apache.org/usermanual/glossary.html">For jMeter</a> (from jMeter documentation : 
	 * <i>"JMeter measures the latency from just before sending the request to just after the first response has been received. 
	 * Thus the time includes all the processing needed to assemble the request as well as assembling the first part of the response, 
	 * which in general will be longer than one byte. 
	 * Protocol analysers (such as Wireshark) measure the time when bytes are actually sent/received over the interface.
	 * The JMeter time should be closer to that which is experienced by a browser or other application client.."</i>)
	 * @return
	 */	
	public void setLatency(long latency) {
		this.latency = latency;
	}
	public long getSizeInOctet() {
		return sizeInOctet;
	}
	/**
	 * Size of the result of the request, in bytes
	 * @param sizeInOctet
	 */
	public void setSizeInOctet(long sizeInOctet) {
		this.sizeInOctet = sizeInOctet;
	}
	public String getStatus() {
		return status;
	}
	/**
	 * An arbitrary status return by the injector for this sample
	 * @param status
	 */
	public void setStatus(String status) {
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
	/**
	 * Value of the assertion result : true or false. True if the assertion has been verified, false instead
	 * @param assertResult
	 */
	public void setAssertResult(boolean assertResult) {
		this.assertResult = assertResult;
	}
	public long getGrpThreads() {
		return grpThreads;
	}
	/**
	 * Number of concurrent thread currently launched in the same group of thread than the thread that has generated this sample
	 * @param grpThreads
	 */
	public void setGrpThreads(long grpThreads) {
		this.grpThreads = grpThreads;
	}
	
	public long getAllThreads() {
		return allThreads;
	}
	/**
	 * Number of concurrent thread currently launched
	 * @param allThreads
	 */	
	public void setAllThreads(long allThreads) {
		this.allThreads = allThreads;
	}
	@Override
	public String toString() {
		return "org.centralperf.model.Sample ["+id+"]["+run.getId()+"]["+timestamp+"]["+elapsed+"]["+sampleName+"]["+returnCode+"]["+latency+"]["+sizeInOctet+"]["+assertResult+"]["+status+"]["+grpThreads+"]["+allThreads+"]";
	}
	
	@Override
	public int compareTo(Sample s) {
		//Sort by ID (default)
		return this.id.compareTo(s.getId());
	}
}
