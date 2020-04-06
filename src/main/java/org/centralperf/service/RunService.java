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

import org.centralperf.exception.ConfigurationException;
import org.centralperf.helper.CSVHeaderInfo;
import org.centralperf.model.SampleDataBackendTypeEnum;
import org.centralperf.model.dao.Run;
import org.centralperf.model.dao.Sample;
import org.centralperf.model.dao.ScriptVariable;
import org.centralperf.model.dao.ScriptVersion;
import org.centralperf.repository.RunRepository;
import org.centralperf.repository.ScriptVariableRepository;
import org.centralperf.repository.ScriptVersionRepository;
import org.centralperf.sampler.api.SamplerRunJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * All services to manage runs : start, end, copy, list....
 *
 * @since 1.0
 */
@Service
public class RunService {

    private static final Logger log = LoggerFactory.getLogger(RunService.class);
    @Value("${centralperf.backend}")
    private SampleDataBackendTypeEnum sampleDataBackendType;
    @Resource
    private RunRepository runRepository;
    @Resource
    private CSVResultService runResultService;
    @Resource
    private ScriptLauncherService scriptLauncherService;
    @Resource
    private ElasticSearchService elasticSearchService;

    @Resource
    private ScriptVersionRepository scriptVersionRepository;

    @Value("${centralperf.csv.field-separator}")
    private String csvSeparator;
    @Resource
    private ScriptVariableRepository scriptVariableRepository;

	/**
	 * Close the run when the launcher has finished. Set the end date and gets job output logs
	 * For Cron-ed runs, do not set the run as finished
	 *
	 * @param runId Id of the run to end
	 * @param job   Job associated with current run
	 */
	public void endRun(Long runId, SamplerRunJob job) {
		Run run = runRepository.findById(runId).orElse(null);
		if (run != null) {
			log.debug("Ending run " + run.getLabel());
			run.setRunning(false);
			run.setLastEndDate(new Date());
			run.setProcessOutput(job.getProcessOutput());

			if (!isCronRun(run)) {
				run.setFinished(true);
			}
			runRepository.save(run);
		}
	}

    /**
     * Force a run to terminate
     *
     * @param run run to terminate
     */
    public void abortRun(Run run) {
        log.debug("Aborting run " + run.getLabel());
        run.setRunning(false);
        run.setLastEndDate(new Date());
        run.setFinished(true);
        String comment = run.getComment() == null ? "INTERRUPTED BY USER !!!" : "INTERRUPTED BY USER !!!\r\n" + run.getComment();
        run.setComment(comment);
        runRepository.save(run);
    }

    /**
     * Cancel a run
     *
     * @param run run to cance
     */
    public void cancelRun(Run run) {
        log.debug("Canceling run " + run.getLabel());
        if (run.isLaunched()) {
            if (run.isRunning()) {
                scriptLauncherService.abortRun(run);
            } else if (isPlannedRun(run)) { // Run was scheduled and the user want to reset it
                scriptLauncherService.cancelScheduleRun(run);
                run.setScheduledDate(null);
                run.setLaunched(false);
                runRepository.save(run);
            } else if (isCronRun(run)) {
                if (run.getLastStartDate() != null) { // It has been running at least one time
                    run.setFinished(true);
                } else {
                    run.setScheduleCronExpression(null);
                    run.setLaunched(false);
                }
                scriptLauncherService.cancelScheduleRun(run);
                runRepository.save(run);
            }
        }
    }

    public void scheduleRun(Run run, String cronExpression) throws ConfigurationException {
        run.setScheduleCronExpression(cronExpression);
        runRepository.save(run);
        scriptLauncherService.launchRun(run);
    }

    public void scheduleRun(Run run, Date scheduledDate) throws ConfigurationException {
        run.setScheduledDate(scheduledDate);
        runRepository.save(run);
        scriptLauncherService.launchRun(run);
    }

    /**
     * Create a copy of a run and save it to the persistence layer (to launch it again for example)
     *
     * @param runId Id of the job to duplicate
     */
    public Run copyRun(Long runId) {
        Run run = runRepository.findById(runId).orElse(null);
        if (run != null) {
            Run newRun = new Run();
			newRun.setLabel(run.getLabel());
			newRun.setLaunched(false);
			newRun.setRunning(false);
			newRun.setScriptVersion(run.getScriptVersion());
			newRun.setSampleDataBackendType(sampleDataBackendType);
			newRun.setProject(run.getProject());
			List<ScriptVariable> scriptVariables = run.getCustomScriptVariables();
			if (scriptVariables != null && scriptVariables.size() > 0) {
				List<ScriptVariable> customScriptVariables = new ArrayList<>();
				for (ScriptVariable scriptVariable : run.getCustomScriptVariables()) {
					customScriptVariables.add(scriptVariable.clone());
				}
				newRun.setCustomScriptVariables(customScriptVariables);
			}
			runRepository.save(newRun);
			return newRun;
		} else {
			return null;
		}
	}

	/**
	 * Updates a variable for the run
	 *
	 * @param runId       ID of the run to update
	 * @param newVariable variable to insert or update
	 */
	public void updateRunVariable(Long runId, ScriptVariable newVariable) {
		Run run = runRepository.findById(runId).orElse(null);

		if (run != null) {
			// Searching for variable in current run custom variables
			for (ScriptVariable customVariable : run.getCustomScriptVariables()) {
				// Already a custom variable with this name => update it
				if (customVariable.getName().equals(newVariable.getName())) {
					customVariable.setValue(newVariable.getValue());
					scriptVariableRepository.save(customVariable);
					return;
				}
			}

			// nothing found ? => add the variable to the script
			List<ScriptVariable> customVariables = run.getCustomScriptVariables();
			customVariables.add(newVariable);
			run.setCustomScriptVariables(customVariables);
			runRepository.save(run);
		}
	}

	/**
	 * Return last X runs accross all projects, ordered by startDate desc (newer first)
	 *
	 * @return A list of run, limited to X runs
	 */
	public List<Run> getLastRuns() {
		return runRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "lastStartDate"))).getContent();
	}

	/**
	 * Get current active runs (running)
	 *
	 * @return List of active runs
	 */
	public List<Run> getActiveRuns() {
		return runRepository.findByRunning(true);
	}

	/**
	 * Get current running Runs
	 *
	 * @return List of running runs
	 */
	public List<Run> getRunningRuns() {
		return runRepository.findByRunning(true);
	}

	/**
	 * Get current schedule runs (cron or to be launched in future)
	 *
	 * @return List of schedule runs
	 */
	public List<Run> getScheduleRuns() {
		return runRepository.findActiveAndScheduledAfter(new Date());
	}

	/**
	 * Get a CSV file content and creates the samples from this CSV content
	 *
	 * @param run        Run to fullfil with CSV content
	 * @param csvContent CSV formatted content. Must be formatted according to csv.field_separator property and CSVHeaderInfo.CSV_HEADER_* headers
	 */
	public void insertResultsFromCSV(Run run, String csvContent) {
		runResultService.saveResults(run, csvContent);
		runRepository.save(run);
	}

	/**
	 * Get run result as a string formatted like CSV
	 *
	 * @param run Target run
	 * @return All samples information as CSV format (comma separated)
	 */
	public String getResultAsCSV(Run run) {
		StringBuilder CSVContent = new StringBuilder(
				CSVHeaderInfo.CSV_HEADER_TIMESTAMP + csvSeparator +
						CSVHeaderInfo.CSV_HEADER_ELAPSED + csvSeparator +
						CSVHeaderInfo.CSV_HEADER_SAMPLENAME + csvSeparator +
						CSVHeaderInfo.CSV_HEADER_STATUS + csvSeparator +
						CSVHeaderInfo.CSV_HEADER_RESPONSECODE + csvSeparator +
						CSVHeaderInfo.CSV_HEADER_ASSERTRESULT + csvSeparator +
						CSVHeaderInfo.CSV_HEADER_SIZEINBYTES + csvSeparator +
						CSVHeaderInfo.CSV_HEADER_GROUPTHREADS + csvSeparator +
						CSVHeaderInfo.CSV_HEADER_ALLTHREADS + csvSeparator +
						CSVHeaderInfo.CSV_HEADER_LATENCY + csvSeparator +
						CSVHeaderInfo.CSV_HEADER_SAMPLEID + csvSeparator +
						CSVHeaderInfo.CSV_HEADER_RUNID + "\r\n");
		for (Sample sample : run.getSamples()) {
			// TODO : Use String.format
			CSVContent
					.append(sample.getTimestamp().getTime())
					.append(csvSeparator).append(sample.getElapsed())
					.append(csvSeparator).append(sample.getSampleName())
					.append(csvSeparator).append(sample.getStatus())
					.append(csvSeparator).append(sample.getReturnCode())
					.append(csvSeparator).append(sample.isAssertResult())
					.append(csvSeparator).append(sample.getSizeInOctet())
					.append(csvSeparator).append(sample.getGrpThreads())
					.append(csvSeparator).append(sample.getAllThreads())
					.append(csvSeparator).append(sample.getLatency())
					.append(csvSeparator).append(sample.getId())
					.append(csvSeparator).append(run.getId()).append("\r\n");
		}
		return CSVContent.toString();
	}

	/**
	 * Import a run from CSV content
	 */
	public void importRun(Run run, String CSVContent) {

		// Import CSV
		insertResultsFromCSV(run, CSVContent);

		// Set script version
		scriptVersionRepository.findById(run.getId()).ifPresent(
				scriptVersion -> {
					run.setScriptVersion(scriptVersion);

					// Update the run to mark it as launched
					// Set start date for imported CSV files
					run.setLastStartDate(run.getSamples().get(0).getTimestamp());
					run.setLastEndDate(run.getSamples().get(run.getSamples().size() - 1).getTimestamp());
					run.setLaunched(true);
					run.setProcessOutput("Results uploaded by user on " + new Date());
					run.setSampleDataBackendType(SampleDataBackendTypeEnum.DEFAULT);

					// Insert into persistence
					runRepository.save(run);
				});
    }

	/**
	 * Create a new Run based on pre-populated bean
	 *
	 * @param run
	 * @return persisted run
	 */
	@Transactional
	public Run createNewRun(Run run) {
		ScriptVersion scriptVersion = scriptVersionRepository.findById(run.getScriptVersion().getId()).orElse(null);
		if (scriptVersion != null) {
			run.setScriptVersion(scriptVersion);
			run.setSampleDataBackendType(sampleDataBackendType);
			return runRepository.save(run);
        } else {
            return null;
        }
    }

    /**
     * Remove run and any associated data (from ES for example)
     *
     * @param runId If of the run to remove
	 */
	@Transactional
	public void deleteRun(Long runId) {
		Run run = runRepository.findById(runId).orElse(null);
        if (run != null) {

            // Remove run from schedule
            if (isCronRun(run) || isDelayedRun(run)) {
                scriptLauncherService.cancelScheduleRun(run);
            }

            // Delete run in persistance
            runRepository.deleteById(runId);

            // Remove ES documents if necessary
            if (SampleDataBackendTypeEnum.ES.equals(run.getSampleDataBackendType())) {
                elasticSearchService.deleteRun(run);
            }
        }
    }

    public boolean isDelayedRun(Run run) {
        return isPlannedRun(run) || isCronRun(run);
    }

    public boolean isPlannedRun(Run run) {
        return run.getScheduledDate() != null;
    }

    public boolean isCronRun(Run run) {
        return run.getScheduleCronExpression() != null;
	}
}
