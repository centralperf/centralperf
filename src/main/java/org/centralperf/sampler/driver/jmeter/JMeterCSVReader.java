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

package org.centralperf.sampler.driver.jmeter;

import org.centralperf.helper.CSVHeaderInfo;
import org.centralperf.model.dao.Run;
import org.centralperf.model.dao.Sample;
import org.centralperf.service.CSVResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * JMeterCSVReader analyses JMeter CSV based format and send it to runResultService
 */
public class JMeterCSVReader {

	private CSVResultService runResultService;
	private static final Logger log = LoggerFactory.getLogger(JMeterCSVReader.class);
    private long nbSamples=0;
    private CSVHeaderInfo headerInfo;
    private Run run;

	public JMeterCSVReader(CSVResultService runResultService, Run run) {
		this.run=run;
		this.runResultService=runResultService;
	}
	
	/**
	 * Process a line of result and convert it to a Sample
	 * @param line CSV-formatted string
	 */
	protected void processLine(String line) {
		log.debug("Processing line : "+line);
		if(runResultService.isHeaderLine(line)){
			this.headerInfo = new CSVHeaderInfo(CSVResultService.splitCSVLine(line,runResultService.getCsvSeparator()));
		} else if(this.headerInfo != null){
			Sample sample = runResultService.buildSampleFromCSVLine(headerInfo, line);
			if(sample != null){
				this.nbSamples++;
				log.debug("Saving sample ["+this.nbSamples+"] in run ["+this.run.getId()+"]");
				runResultService.addSample(this.run, sample);
			}
		} else {
			log.debug(String.format("Headers not yet found, dropping line %s", line));
		}
	}
}
