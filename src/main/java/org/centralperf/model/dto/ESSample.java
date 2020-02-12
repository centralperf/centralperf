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

package org.centralperf.model.dto;

import java.util.Date;

import org.centralperf.model.dao.Sample;

/**
 * Sample result to send to ElasticSearch as JSON
 * @since 1.1
 */
public class ESSample {

	public Long projectId;
	public Long runId;
	public Date timestamp;
	public long elapsed;
	public String sampleName;
	public long returnCode;
	public long latency;
	public long sizeInOctet;
	public boolean assertResult;
	public long grpThreads;
	public long allThreads;
	public String status;
	
	public ESSample(Sample sample) {
		this.projectId = sample.getRun().getProject().getId();
		this.runId = sample.getRun().getId();
		this.timestamp = sample.getTimestamp();
		this.elapsed = sample.getElapsed();
		this.sampleName = sample.getSampleName();
		this.returnCode = sample.getReturnCode();
		this.latency = sample.getLatency();
		this.sizeInOctet = sample.getSizeInOctet();
		this.assertResult = sample.isAssertResult();
		this.grpThreads = sample.getGrpThreads();
		this.allThreads = sample.getAllThreads();
		this.status = sample.getStatus();
	}
	
}