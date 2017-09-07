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

package org.centralperf.service;

import org.springframework.core.io.Resource;

/**
 * Reference sample files for bootstraping
 * TODO : Load Kibana files directly from listing folders instead of a resource bean per file
 * @since 1.0
 */
public class BootstrapServiceFiles {

	private Resource sampleJMXFile;
	private Resource sampleGatlingFile;
	
	private Resource kibanaDashboardOverview;
	private Resource kibanaVisualizationGlobalMetrics;
	private Resource kibanaVisualizationResponseTimePerTime;
	private Resource kibanaVisualizationResponseTimePerSample;
	private Resource kibanaCentralPerfIndexPattern;
	
	public Resource getSampleJMXFile() {
		return sampleJMXFile;
	}

	public void setSampleJMXFile(org.springframework.core.io.Resource sampleJMXFile) {
		this.sampleJMXFile = sampleJMXFile;
	}

	public Resource getSampleGatlingFile() {
		return sampleGatlingFile;
	}

	public void setSampleGatlingFile(
			org.springframework.core.io.Resource sampleGatlingFile) {
		this.sampleGatlingFile = sampleGatlingFile;
	}

	public Resource getKibanaVisualizationGlobalMetrics() {
		return kibanaVisualizationGlobalMetrics;
	}

	public void setKibanaVisualizationGlobalMetrics(org.springframework.core.io.Resource kibanaVisualizationGlobalMetrics) {
		this.kibanaVisualizationGlobalMetrics = kibanaVisualizationGlobalMetrics;
	}

	public Resource getKibanaVisualizationResponseTimePerTime() {
		return kibanaVisualizationResponseTimePerTime;
	}

	public void setKibanaVisualizationResponseTimePerTime(
			org.springframework.core.io.Resource kibanaVisualizationResponseTimePerTime) {
		this.kibanaVisualizationResponseTimePerTime = kibanaVisualizationResponseTimePerTime;
	}

	public Resource getKibanaVisualizationResponseTimePerSample() {
		return kibanaVisualizationResponseTimePerSample;
	}

	public void setKibanaVisualizationResponseTimePerSample(
			org.springframework.core.io.Resource kibanaVisualizationResponseTimePerSample) {
		this.kibanaVisualizationResponseTimePerSample = kibanaVisualizationResponseTimePerSample;
	}

	public Resource getKibanaDashboardOverview() {
		return kibanaDashboardOverview;
	}

	public void setKibanaDashboardOverview(Resource kibanaDashboardOverview) {
		this.kibanaDashboardOverview = kibanaDashboardOverview;
	}

	public Resource getKibanaCentralPerfIndexPattern() {
		return kibanaCentralPerfIndexPattern;
	}

	public void setKibanaCentralPerfIndexPattern(Resource kibanaCentralPerfIndexPattern) {
		this.kibanaCentralPerfIndexPattern = kibanaCentralPerfIndexPattern;
	}
	
	
}
