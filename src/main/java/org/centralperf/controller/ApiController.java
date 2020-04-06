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

import freemarker.ext.beans.BeansWrapper;
import org.centralperf.controller.exception.ControllerValidationException;
import org.centralperf.helper.CronExpression;
import org.centralperf.helper.view.ExcelOOXMLView;
import org.centralperf.model.dao.Run;
import org.centralperf.model.dto.CronFormatResponse;
import org.centralperf.model.dto.RunStatus;
import org.centralperf.model.graph.RunStats;
import org.centralperf.repository.RunRepository;
import org.centralperf.service.RunService;
import org.centralperf.service.RunStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/** 
 * The API controller manage the CP API (REST/CSV/Excel/HTML...) output
 * 
 * @since 1.0
 * 
 */
@Controller
public class ApiController {
	
	
	@Resource
	private RunStatisticsService runStatService;

	@Resource
	private RunRepository runRepository;

	@Resource
	private RunService runService;

	private final ApplicationContext applicationContext;

	private static final Logger log = LoggerFactory.getLogger(ApiController.class);

	public ApiController(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * export summary chart csv.
	 *
	 * @param runId ID of the run (from URI)
	 * @return CSV with Response Time and concurrent requests datas
	 */
	@RequestMapping(value = "/api/getSumChartCSV/{runId}", method = RequestMethod.GET, produces = "text/csv")
	@ResponseBody
	public String getSumChartCSV(@PathVariable("runId") Long runId) {
		String csv = "";
		try {
			csv = runStatService.getSummaryGraph(runId);
		} catch (ExecutionException ee) {
			log.error("CSV data could not be retrieve from cache:" + ee.getMessage());
		}
		return csv;
	}

	/**
	 * export csv with average response time of each sample.
	 *
	 * @param runId ID of the run (from URI)
	 * @return CSV with average Response Time
	 */
	@RequestMapping(value = "/api/getRTChartCSV/{runId}", method = RequestMethod.GET, produces = "text/csv")
	@ResponseBody
	public String getRTChartCSV(@PathVariable("runId") Long runId) {
		String csv = "";
		try {
			csv = runStatService.getResponseTimeGraph(runId);
		} catch (ExecutionException ee) {
			log.error("CSV data could not be retrieve from cache:" + ee.getMessage());
		}
		return csv;
	}

	/**
	 * export csv with average response size of each sample.
	 *
	 * @param runId ID of the run (from URI)
	 * @return CSV with average Response size
	 */
	@RequestMapping(value = "/api/getRSChartCSV/{runId}", method = RequestMethod.GET, produces = "text/csv")
	@ResponseBody
	public String getRSChartCSV(@PathVariable("runId") Long runId) {
		String csv = "";
		try {
			csv = runStatService.getResponseSizeGraph(runId);
		} catch (ExecutionException ee) {
			log.error("CSV data could not be retrieve from cache:" + ee.getMessage());
		}
		return csv;
	}


	/**
	 * export csv with average response size of each sample.
	 *
	 * @param runId ID of the run (from URI)
	 * @return CSV with average Response size
	 */
	@RequestMapping(value = "/api/getERChartCSV/{runId}", method = RequestMethod.GET, produces = "text/csv")
	@ResponseBody
	public String getERChartCSV(@PathVariable("runId") Long runId) {
		String csv = "";
		try {
			csv = runStatService.getErrorRateGraph(runId);
		} catch (ExecutionException ee) {
			log.error("CSV data could not be retrieve from cache:" + ee.getMessage());
		}
		return csv;
	}

	/**
	 * List run data statistics as JSON
	 *
	 * @param runId ID of the run (from URI)
	 * @return JSON run statistics
	 */
	@RequestMapping(value = "/api/getRunStatsJSON/{runId}", method = RequestMethod.GET)
	@ResponseBody
	public RunStats getRunStatsJSON(@PathVariable("runId") Long runId) {
		RunStats temp = null;
		try {
			temp = runStatService.getRunStats(runId);
		} catch (ExecutionException ee) {
			log.error("JSON data could not be retrieve from cache:"+ee.getMessage());
		}
		return temp;
	}

    /**
     * Download results from a RUN as a file (CSV or other)
     * @param runId	ID of the run (from URI)
     * @return The JTL/CSV file as text/csv file content type
	 */
	@RequestMapping(
			value = {
					"/project/*/run/{runId}/results",
    				"/api/getRunResultsCSV/{runId}",
    				}, 
    		method = RequestMethod.GET, 
    		produces = "text/csv")
    public ResponseEntity<String> getRunResultsAsCSV(
            @PathVariable("runId") Long runId
    		) throws ControllerValidationException {
        Run run = runRepository.findById(runId).orElseThrow(() -> new ControllerValidationException(String.format("Run with id %s does not exists", runId)));
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Disposition", "attachment; filename=results.csv");
        String CSVContent = runService.getResultAsCSV(run);
        return new ResponseEntity<>(CSVContent, responseHeaders, HttpStatus.CREATED);
    }    	
    
    /**
     * Display all samples of a RUN as a HTML page
     * @param runId	ID of the run (from URI)
     * @return	Name of the view for samples as HTML
	 */
	@RequestMapping(
			value = {
					"/project/*/run/{runId}/samples",
					"/run/{runId}/samples",
    				"/api/getRunResultsHTML/{runId}"
    		}, 
    		method = RequestMethod.GET)
    public String getRunResultsAsHTML(
            @PathVariable("runId") Long runId,
    		Model model
    		) throws ControllerValidationException {
    	 Run run = runRepository.findById(runId).orElseThrow(() -> new ControllerValidationException(String.format("Run with id %s does not exists", runId)));
    	 model.addAttribute("run", run);
    	 
    	 // Add statics for CSVHeaderInfo
    	 model.addAttribute("statics", BeansWrapper.getDefaultInstance().getStaticModels());
    	 return "runSamples";
    }    
    
    /**
     * Get run results as an Excel document (XSLX)
     * The file name for now is centralperf.xlsx
     * @param mav	ModelAndView will be used to return an Excel view
     * @param runId ID of the run (from URI)
     * @return A view that will be resolved as an Excel view by the view resolver
	 */
	@RequestMapping(value = {
			"/project/*/run/{runId}/centralperf.xlsx",
			"/api/getRunResultsHTML/{runId}/centralperf.xlsx"
    	}
    	, method = RequestMethod.GET)
    public ModelAndView getRunResultsAsExcel(
    		ModelAndView mav,
            @PathVariable("runId") Long runId) throws ControllerValidationException {
		Run run = runRepository.findById(runId).orElseThrow(() -> new ControllerValidationException(String.format("Run with id %s does not exists", runId)));
    	
    	// get the view and setup
    	ExcelOOXMLView excelView = applicationContext.getBean(ExcelOOXMLView.class);
	    excelView.setUrl("/WEB-INF/views/xlsx/centralperf_template");
	    mav.getModel().put("run", run);
    	mav.setView(excelView);
        // return a view which will be resolved by an excel view resolver
		return mav;
	}

	/**
	 * Run status
	 *
	 * @param runId ID of the run (from URI)
	 * @return Status for selected run
	 */
	@RequestMapping(value = {"/api/run/{runId}/status"}, method = RequestMethod.GET)
	public @ResponseBody
	RunStatus getRunStatus(@PathVariable("runId") Long runId) throws ControllerValidationException {
		Run run = runRepository.findById(runId).orElseThrow(() -> new ControllerValidationException(String.format("Run with id %s does not exists", runId)));
		RunStatus runStatus = new RunStatus();
		runStatus.setRunId(run.getId());
		runStatus.setRunning(run.isRunning());
		runStatus.setStartDate(run.getLastStartDate());
		return runStatus;
	}

	/**
	 * Validate a cron expression and return info about it
	 *
	 * @param cronExpression Cron based expression (6 terms)
	 * @return A response with next date, human readable and validation status
	 */
	@RequestMapping(value = {"/api/cron/format"}, method = RequestMethod.GET)
	public @ResponseBody
	CronFormatResponse validateAndFormat(@RequestParam("expression") String cronExpression) {
		CronFormatResponse response = new CronFormatResponse();
		response.setCronExpression(cronExpression);
		try {
			CronExpression.validateCronExpression(cronExpression);
			response.setValid(true);
			response.setNext(CronExpression.getNextIteration(cronExpression));
			response.setHumanReadable(CronExpression.asHumanReadable(cronExpression));
		} catch (IllegalArgumentException e) {
			response.setValid(false);
			response.setValidationErrorMesssage(e.getMessage());
		}
		return response;
	}

}
