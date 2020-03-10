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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * JMeterCSVReader reads JMeter CSV output file every seconds to extract
 * data and push it the CSV Reader
 *
 * @since 1.0
 */
public class JMeterCSVReaderTimerTask extends TimerTask {

    private JMeterCSVReader reader;

    private static final Logger log = LoggerFactory.getLogger(JMeterCSVReaderTimerTask.class);

    private File csvFile;
    private long csvFileLastTimestamp;
    private long csvFileLastLength;
    private long nbSamples = 0;

    private CSVHeaderInfo headerInfo;

    public JMeterCSVReaderTimerTask(File csvFile, JMeterCSVReader reader) {
        csvFileLastLength = csvFile.length();
        this.csvFile = csvFile;
        this.csvFileLastTimestamp = csvFile.lastModified();
        this.reader = reader;
    }

    public static JMeterCSVReaderTimerTask newReader(File csvFile, JMeterCSVReader reader) {
        JMeterCSVReaderTimerTask jMeterCSVReaderTimerTaskTask = new JMeterCSVReaderTimerTask(csvFile, reader);
        Timer timer = new Timer();

        // repeat the check every second
        timer.schedule(jMeterCSVReaderTimerTaskTask, new Date(), 1000);

        log.debug("Start reading JMeter CSV file every second");
        return jMeterCSVReaderTimerTaskTask;
    }

    public final void run() {
        log.debug("Start reading of CSV File");
        long tsp = csvFile.lastModified();
        if (this.csvFileLastTimestamp != tsp) {
            this.csvFileLastTimestamp = tsp;
            processCSVChange();
        }
    }

    /**
     * Triggered when the monitored file has changed
     * Can be overrided if necessary
     */
    private void processCSVChange() {

        RandomAccessFile access = null;
        try {
            log.debug("Processing changes on " + this.csvFile.getCanonicalPath());
            access = new RandomAccessFile(this.csvFile, "r");
			access.seek(Math.min(this.csvFile.length(), this.csvFileLastLength));
        } catch (Exception e) {
            log.error("Error while reading " + this.csvFile.getPath() + ": " + e.getMessage(), e);
        }

        String line;
        try {
            log.debug("Reading new CSV lines");
			assert access != null;
			while ((line = access.readLine()) != null) {
                this.reader.processLine(line);
            }
            this.csvFileLastLength = this.csvFile.length();
        } catch (IOException iOE) {
            log.error("Error while reading line on " + this.csvFile.getPath() + ": " + iOE.getMessage(), iOE);
        }

        try {
            access.close();
        } catch (IOException iOE) {
            log.error("Error while closing " + this.csvFile.getPath() + ": " + iOE.getMessage(), iOE);
        }
    }
}
