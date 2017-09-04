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

package org.centralperf.model.graph;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean to store all statistics for a specfic run (number of sample, bandwith...)
 */
public class RunStats {

	private static final Logger log = LoggerFactory.getLogger(RunStats.class);
	
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
	private String runOutput;
	private boolean running;
	
	public RunStats(Object[] parameters, String runOutput, boolean running){
		try{
			this.numberOfSample=(parameters[0]==null)?0:Integer.parseInt(parameters[0].toString());
			this.totalBandwith=(parameters[1]==null)?0:Long.parseLong(parameters[1].toString());
			this.firstSampleDate=(parameters[2]==null)?null:(Date)parameters[2];
			this.lastSampleDate=(parameters[3]==null)?null:(Date)parameters[3];
			this.averageResponseTime=(parameters[4]==null)?0:(long)Double.parseDouble(parameters[4].toString());
			this.averageLatency=(parameters[5]==null)?0:(long)Double.parseDouble(parameters[5].toString());
			this.maxUsers=(parameters[6]==null)?0:Integer.parseInt(parameters[6].toString());
			this.duration=(this.lastSampleDate==null||this.firstSampleDate==null)?0:this.lastSampleDate.getTime()-this.firstSampleDate.getTime();
			this.requestPerSecond=(this.duration==0)?0:this.numberOfSample/(this.duration/1000F);
			this.currentBandwith=(this.duration/1000L==0)?this.totalBandwith:this.totalBandwith/(this.duration/1000L);
			long numberOfErrors=(parameters[7]==null)?0:Integer.parseInt(parameters[7].toString());
			this.errorRate=(this.numberOfSample==0)?0:(100 * numberOfErrors) / this.numberOfSample;
			this.setRunOutput(runOutput);
			this.setRunning(running);
		}
		catch (NullPointerException npE) {log.error("Missing data",npE);}
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
		if(temp<1024){return temp+" kB";}
		return Math.round((temp/1024F)*100)/100F+" MB";
	}
	public long getCurrentBandwith() {return currentBandwith;}
	public void setCurrentBandwith(long currentBandwith) {this.currentBandwith = currentBandwith;}
	
	public String getTotalBandwithWithUnit(){
		float temp = Math.round((this.totalBandwith/1024F)*100)/100F;
		if(temp<1024){return temp+" kB";}
		return Math.round((temp/1024F)*100)/100F+" MB";
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
	
	public String getRunOutput() {return runOutput;}
	public void setRunOutput(String runOutput) {this.runOutput = runOutput;}
	
	public boolean isRunning() {return running;}
	public void setRunning(boolean running) {this.running = running;}
	
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
