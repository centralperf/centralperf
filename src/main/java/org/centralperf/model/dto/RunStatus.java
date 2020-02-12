package org.centralperf.model.dto;

import java.util.Date;

public class RunStatus {

	private long runId;
	private Date startDate;
	private boolean isRunning;
	
	public long getRunId() {
		return runId;
	}
	public void setRunId(long runId) {
		this.runId = runId;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public boolean isRunning() {
		return isRunning;
	}
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}
