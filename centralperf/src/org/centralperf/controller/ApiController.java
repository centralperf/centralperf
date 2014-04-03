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

package org.centralperf.controller;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.centralperf.model.graph.RunStats;
import org.centralperf.service.RunStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/** 
 * The API controller  manage the CP API rest output
 * 
 * @since 1.0
 * 
 */
@Controller
public class ApiController {
	
	
	@Resource
	private RunStatisticsService runStatService;
	
	private static final Logger log = LoggerFactory.getLogger(ApiController.class);
	
	/**
	 * export summary chart csv.
	 * @param runId	ID of the run (from URI)
	 * @param model	Model prepared for the new project form view
	 * @return CSV with Response Time and concurrent requests datas
	 * @throws IOException 
	 */
	@RequestMapping(value = "/api/getSumChartCSV/{runId}", method = RequestMethod.GET, produces = "text/csv")
	@ResponseBody
	public String getSumChartCSV(@PathVariable("runId") Long runId,Model model,HttpServletResponse response) throws IOException {
		String csv="";
		try {csv = runStatService.getSummaryGraph(runId);} 
		catch (ExecutionException ee) {
			log.error("CSV data could not be retrieve from cache:"+ee.getMessage());
		}
		return csv;
	}
	
	/**
	 * export csv with average response time of each sample.
	 * @param runId	ID of the run (from URI)
	 * @param model	Model prepared for the new project form view
	 * @return CSV with average Response Time
	 * @throws IOException 
	 */
	@RequestMapping(value = "/api/getRTChartCSV/{runId}", method = RequestMethod.GET, produces = "text/csv")
	@ResponseBody
	public String getRTChartCSV(@PathVariable("runId") Long runId,Model model,HttpServletResponse response) throws IOException {
		String csv="";
		try {csv = runStatService.getResponseTimeGraph(runId);} 
		catch (ExecutionException ee) {
			log.error("CSV data could not be retrieve from cache:"+ee.getMessage());
		}
		return csv;
	}
	
	/**
	 * export csv with average response size of each sample.
	 * @param runId	ID of the run (from URI)
	 * @param model	Model prepared for the new project form view
	 * @return CSV with average Response size
	 * @throws IOException 
	 */
	@RequestMapping(value = "/api/getRSChartCSV/{runId}", method = RequestMethod.GET, produces = "text/csv")
	@ResponseBody
	public String getRSChartCSV(@PathVariable("runId") Long runId,Model model,HttpServletResponse response) throws IOException {
		String csv="";
		try {csv = runStatService.getResponseSizeGraph(runId);} 
		catch (ExecutionException ee) {
			log.error("CSV data could not be retrieve from cache:"+ee.getMessage());
		}
		return csv;
	}
	
	
	/**
	 * export csv with average response size of each sample.
	 * @param runId	ID of the run (from URI)
	 * @param model	Model prepared for the new project form view
	 * @return CSV with average Response size
	 * @throws IOException 
	 */
	@RequestMapping(value = "/api/getERChartCSV/{runId}", method = RequestMethod.GET, produces = "text/csv")
	@ResponseBody
	public String getERChartCSV(@PathVariable("runId") Long runId,Model model,HttpServletResponse response) throws IOException {
		String csv="";
		try {csv = runStatService.getErrorRateGraph(runId);} 
		catch (ExecutionException ee) {
			log.error("CSV data could not be retrieve from cache:"+ee.getMessage());
		}
		return csv;
	}
	
	/**
	 * List run data statistics as JSON 
	 * @param runId	ID of the run (from URI)
	 * @param model
	 * @return JSON run statistics 
	 */
	@RequestMapping(value ="/api/getRunStatsJSON/{runId}", method=RequestMethod.GET)  
	@ResponseBody 
	public RunStats getJsonProjectList(@PathVariable("runId") Long runId,Model model){
		RunStats temp=null;
		try {temp=runStatService.getRunStats(runId);}
		catch (ExecutionException ee) {
				log.error("JSON data could not be retrieve from cache:"+ee.getMessage());
		}
		return temp;
	}
}
