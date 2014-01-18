package org.centralperf.model;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunStatistics {

	private static final Logger log = LoggerFactory.getLogger(RunStatistics.class);
	
	private int numberOfSample;
	private Date firstSampleDate;
	private Date lastSampleDate;
	private long currentUsers;
	private long maxUsers;
	private long currentBandwith;
	private long totalBandwith;
	private long averageResponseTime;
	private long averageLatency;
	private float requestPerSecond;
	private float errorRate;
	private long  duration;
	
	public RunStatistics(Object[] parameters){
		try{
			this.numberOfSample=Integer.parseInt(parameters[0].toString());
			this.totalBandwith=Integer.parseInt(parameters[1].toString());
			this.firstSampleDate=(Date)parameters[2];
			this.lastSampleDate=(Date)parameters[3];
			this.averageResponseTime= (long)Double.parseDouble(parameters[4].toString());
			this.averageLatency= (long)Double.parseDouble(parameters[5].toString());
			this.maxUsers=Integer.parseInt(parameters[6].toString());
			this.duration=this.lastSampleDate.getTime()-this.firstSampleDate.getTime();
			this.requestPerSecond=this.numberOfSample/(this.duration/1000F);
			this.currentBandwith=this.totalBandwith/(this.duration/1000L);
			long numberOfErrors=Integer.parseInt(parameters[7].toString());
			this.errorRate=(100 * numberOfErrors) / this.numberOfSample;
		}
		catch (NullPointerException npE) {log.error("Missing data");}
	}
	
	public int getNumberOfSample() {return numberOfSample;}
	public void setNumberOfSample(int numberOfSample) {this.numberOfSample = numberOfSample;}
	
	public Date getLastSampleDate() {return lastSampleDate;}
	public void setLastSampleDate(Date lastSampleDate) {this.lastSampleDate = lastSampleDate;}
	
	public long getCurrentUsers() {return currentUsers;}
	public void setCurrentUsers(long currentUsers) {this.currentUsers = currentUsers;}
	
	public long getMaxUsers() {return maxUsers;}
	public void setMaxUsers(long maxUsers) {this.maxUsers = maxUsers;}
	
	public String getCurrentBandwithWithUnit(){
		float temp = Math.round((this.currentBandwith/1024F)*100)/100F;
		if(temp<1024){return temp+" ko";}
		return Math.round((temp/1024F)*100)/100F+" mo";
	}
	public long getCurrentBandwith() {return currentBandwith;}
	public void setCurrentBandwith(long currentBandwith) {this.currentBandwith = currentBandwith;}
	
	public String getTotalBandwithWithUnit(){
		float temp = Math.round((this.totalBandwith/1024F)*100)/100F;
		if(temp<1024){return temp+" ko";}
		return Math.round((temp/1024F)*100)/100F+" mo";
	}
	public long getTotalBandwith() {return totalBandwith;}
	public void setTotalBandwith(long totalBandwith) {this.totalBandwith = totalBandwith;}
	
	public long getAverageResponseTime() {return averageResponseTime;}
	public void setAverageResponseTime(long averageResponseTime) {this.averageResponseTime = averageResponseTime;}
	
	public long getAverageLatency() {return averageLatency;}
	public void setAverageLatency(long averageLatency) {this.averageLatency = averageLatency;}
	
	public float getRequestPerSecond() {return requestPerSecond;}
	public void setRequestPerSecond(float requestPerSecond) {this.requestPerSecond = requestPerSecond;}
	
	public float getErrorRate() {return errorRate;}
	public void setErrorRate(float errorRate) {this.errorRate = errorRate;}

	public long getDuration() {return duration;}
	public void setDuration(long duration) {this.duration = duration;}
	
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
