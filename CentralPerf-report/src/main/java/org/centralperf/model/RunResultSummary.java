package org.centralperf.model;

import java.util.Date;

public class RunResultSummary {

	private int numberOfSample;
	private Date lastSampleDate;
	
	public int getNumberOfSample() {
		return numberOfSample;
	}
	public void setNumberOfSample(int numberOfSample) {
		this.numberOfSample = numberOfSample;
	}
	public Date getLastSampleDate() {
		return lastSampleDate;
	}
	public void setLastSampleDate(Date lastSampleDate) {
		this.lastSampleDate = lastSampleDate;
	}
	
	
}
