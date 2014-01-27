package org.centralperf.model;

import org.centralperf.model.dao.Run;

public class RunDetail {
	private boolean running = false;
	private String jobOutput = "";
	private RunDetailStatistics runDetailStatistics;
	private RunDetailGraphRt 	runDetailGraphRt;
	private RunDetailGraphSum 	runDetailGraphSum;
	private RunDetailGraphRc    runDetailGraphRc;
	
	public RunDetail(Run run) {
		this.running=run.isRunning();
		this.jobOutput=run.getProcessOutput();
	}
	
	public String getJobOutput() {
		return jobOutput;
	}
	public void setJobOutput(String jobOutput) {
		this.jobOutput = jobOutput;
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public RunDetailStatistics getRunDetailStatistics() {
		return runDetailStatistics;
	}
	public void setRunDetailStatistics(RunDetailStatistics runDetailStatistics) {
		this.runDetailStatistics = runDetailStatistics;
	}

	public RunDetailGraphRt getRunDetailGraphRt() {
		return runDetailGraphRt;
	}

	public void setRunDetailGraphRt(RunDetailGraphRt runDetailGraphRt) {
		this.runDetailGraphRt = runDetailGraphRt;
	}

	public RunDetailGraphSum getRunDetailGraphSum() {
		return runDetailGraphSum;
	}

	public void setRunDetailGraphSum(RunDetailGraphSum runDetailGraphSum) {
		this.runDetailGraphSum = runDetailGraphSum;
	}

	public RunDetailGraphRc getRunDetailGraphRc() {
		return runDetailGraphRc;
	}

	public void setRunDetailGraphRc(RunDetailGraphRc runDetailGraphRc) {
		this.runDetailGraphRc = runDetailGraphRc;
	}
}
