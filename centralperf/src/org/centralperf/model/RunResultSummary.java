package org.centralperf.model;

import java.util.Date;

/**
 * Summary of main indicators for a run 
 * @author Charles Le Gallic
 */
public class RunResultSummary {

	private int numberOfSample;
	private Date lastSampleDate;
	private long currentUsers;
	private long maxUsers;
	private long currentBandwith;
	private long totalBandwith;
	private long averageResponseTime;
	private long averageLatency;
	private float requestPerSecond;
	private float errorRate;
	private long duration;
	
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
	public long getCurrentUsers() {
		return currentUsers;
	}
	public void setCurrentUsers(long currentUsers) {
		this.currentUsers = currentUsers;
	}
	public long getMaxUsers() {
		return maxUsers;
	}
	public void setMaxUsers(long maxUsers) {
		this.maxUsers = maxUsers;
	}
	public long getCurrentBandwith() {
		return currentBandwith;
	}
	public void setCurrentBandwith(long currentBandwith) {
		this.currentBandwith = currentBandwith;
	}
	public long getTotalBandwith() {
		return totalBandwith;
	}
	public void setTotalBandwith(long totalBandwith) {
		this.totalBandwith = totalBandwith;
	}
	public long getAverageResponseTime() {
		return averageResponseTime;
	}
	public void setAverageResponseTime(long averageResponseTime) {
		this.averageResponseTime = averageResponseTime;
	}
	public long getAverageLatency() {
		return averageLatency;
	}
	public void setAverageLatency(long averageLatency) {
		this.averageLatency = averageLatency;
	}
	public float getRequestPerSecond() {
		return requestPerSecond;
	}
	public void setRequestPerSecond(float requestPerSecond) {
		this.requestPerSecond = requestPerSecond;
	}
	public float getErrorRate() {
		return errorRate;
	}
	public void setErrorRate(float errorRate) {
		this.errorRate = errorRate;
	}

	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	@Override
	public String toString() {
		return "Number of samples : " + this.getNumberOfSample() + ","
				+ "Current users :" + this.getCurrentUsers() + ", "
				+ "Max users : " + this.getMaxUsers() + ", "
				+ "Current bandwith : " + this.getCurrentBandwith() + ", "
				+ "Total bandwith : " + this.getTotalBandwith() + ", "
				+ "Average response time : " + this.getAverageResponseTime() + ", "
				+ "Average latency : " + this.getAverageLatency() + ", "
				+ "Requests per seconds : " + this.getRequestPerSecond() + ", "
				+ "Error rate : " + this.getErrorRate() + ", "
				+ "Last sample : " + this.getLastSampleDate() + ","
				+ "Duration : " + this.getDuration()
				;
	}
}
