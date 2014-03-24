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

package org.centralperf.model;

import org.centralperf.model.dao.Run;

/**
 * Detail of run results. Used to provide output and graph info to the views
 */
public class RunDetail {
	private boolean running = false;
	private String jobOutput = "";
	private RunDetailStatistics runDetailStatistics;
	private RunDetailGraphRt 	runDetailGraphRt;
	private RunDetailGraphSum 	runDetailGraphSum;
	private RunDetailGraphRc    runDetailGraphRc;
	private RunDetailGraphError runDetailGraphError;
	
	/**
	 * Default constructor. Require a run 
	 * @param run	Run for the detail
	 */
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

	public RunDetailGraphError getRunDetailGraphError() {
		return runDetailGraphError;
	}

	public void setRunDetailGraphError(RunDetailGraphError runDetailGraphError) {
		this.runDetailGraphError = runDetailGraphError;
	}
}
