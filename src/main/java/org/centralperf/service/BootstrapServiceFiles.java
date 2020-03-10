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

import com.google.common.io.CharStreams;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Reference sample files for bootstraping
 * TODO : Load Kibana files directly from listing folders instead of a resource bean per file
 * @since 1.0
 */
@Service
public class BootstrapServiceFiles {


	@Value("${jmeter.sample-file}")
	Resource sampleJMXFile;

	@Value("${gatling.sample-file}")
	Resource sampleGatlingFile;

	@Value("${centralperf.elastic.bootstrap.templates.kibana.dashboards.overview}")
	Resource kibanaDashboardOverview;
	@Value("${centralperf.elastic.bootstrap.templates.kibana.visualizations.global-metrics}")
	Resource kibanaVisualizationGlobalMetrics;
	@Value("${centralperf.elastic.bootstrap.templates.kibana.visualizations.response-time-per-time}")
	Resource kibanaVisualizationResponseTimePerTime;
	@Value("${centralperf.elastic.bootstrap.templates.kibana.visualizations.response-time-per-sample}")
	Resource kibanaVisualizationResponseTimePerSample;
	@Value("${centralperf.elastic.bootstrap.templates.kibana.patterns.centralperf}")
	Resource kibanaCentralPerfIndexPattern;

	private static final Logger logger = LoggerFactory.getLogger(BootstrapServiceFiles.class);
	
	public String getSampleJMXFile() {
		return getResourceAsString(sampleJMXFile);
	}

	public String getSampleGatlingFile() {
		return getResourceAsString(sampleGatlingFile);
	}

	public String getKibanaVisualizationGlobalMetrics() {
		return getResourceAsString(kibanaVisualizationGlobalMetrics);
	}

	public String getKibanaVisualizationResponseTimePerTime() {
		return getResourceAsString(kibanaVisualizationResponseTimePerTime);
	}

	public String getKibanaVisualizationResponseTimePerSample() {
		return getResourceAsString(kibanaVisualizationResponseTimePerSample);
	}

	public String getKibanaDashboardOverview() {
		return getResourceAsString(kibanaDashboardOverview);
	}

	public String getKibanaCentralPerfIndexPattern() {
		return getResourceAsString(kibanaCentralPerfIndexPattern);
	}

	private String getResourceAsString(Resource resource){
		try {
			return IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8.name());
		} catch (IOException e) {
			logger.error(String.format("Unable to load bootstrap file %s", resource.getFilename()), e);
			return null;
		}
	}
	
	
}
