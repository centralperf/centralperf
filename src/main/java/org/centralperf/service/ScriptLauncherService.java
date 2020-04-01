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
import org.centralperf.helper.BackgroundThreadSessionManager;
import org.centralperf.model.dao.Run;
import org.centralperf.model.dao.ScriptVersion;
import org.centralperf.repository.RunRepository;
import org.centralperf.sampler.api.Sampler;
import org.centralperf.sampler.api.SamplerRunJob;
import org.centralperf.service.task.ScriptLauncherTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * All operation to launch / stop / monitor launched jobs
 *
 * @since 1.0
 */
@Service
public class ScriptLauncherService {

	@Resource
	private RunRepository runRepository;

	@Resource
	private SamplerService samplerService;

	@Resource
	private RunService runService;

	@Resource
	private ThreadPoolTaskScheduler scheduledRunThreadPoolTaskScheduler;

	@Resource
	private BackgroundThreadSessionManager backgroundThreadSessionManager;

	private HashMap<Long, SamplerRunJob> runningJobs = new HashMap<>();

	private static final Logger log = LoggerFactory.getLogger(ScriptLauncherService.class);

	public void launchRun(Run run) throws ConfigurationException {
		if (runService.isDelayedRun(run)) {
			this.launchSchedule(run);
		} else {
			this.launchNow(run);
		}
	}

	public void launchSchedule(Run run) {
		ScriptLauncherTask task = new ScriptLauncherTask(run, this, backgroundThreadSessionManager);
		if (run.getScheduleCronExpression() != null) {
			scheduledRunThreadPoolTaskScheduler.schedule(task, new CronTrigger(run.getScheduleCronExpression()));
		} else if (run.getScheduledDate() != null) {
			scheduledRunThreadPoolTaskScheduler.schedule(task, run.getScheduledDate());
		} else
			throw new IllegalArgumentException("The run me be either scheduled for a delayed start or with a cron expression");
		run.setLaunched(true);
		runRepository.save(run);
	}

	public void launchNow(Long runId) throws ConfigurationException {
		launchNow(runRepository.findById(runId).get());
	}

	public void launchNow(Run run) throws ConfigurationException {
		ScriptVersion scriptVersion = run.getScriptVersion();

		// Get the sampler type
		Sampler sampler = samplerService.getSamplerByUID(run.getScriptVersion().getScript().getSamplerUID());

		log.debug("Launching run " + run.getLabel());
		// Replace variables by their value
		String finalScriptContent = sampler.getScriptProcessor().replaceVariablesInScript(scriptVersion.getContent(), run.getCustomScriptVariables());

		SamplerRunJob job = sampler.getLauncher().launch(finalScriptContent, run);
		runningJobs.put(run.getId(), job);
		run.setLaunched(true);
		run.setRunning(true);
		run.setLastStartDate(new Date());
		runRepository.save(run);
	}

	/**
	 * Abort a running test
	 */
	public void abortRun(Run run) {
		if (run.isRunning()) {
			log.debug("Stopping run [" + run.getId() + "]:" + run.getLabel());
			SamplerRunJob job = getJob(run.getId());
			if (job != null) {
				job.stopProcess();
			}
			runService.abortRun(run);
		}
	}

	public SamplerRunJob getJob(Long runId) {
		return runningJobs.get(runId);
	}

	/**
	 * Called once the job finished
	 */
	public void endJob(SamplerRunJob job) {
		runningJobs
				.entrySet()
				.stream()
				.filter(entry -> job.equals(entry.getValue()))
				.findFirst()
				.map(Map.Entry::getKey)
				.ifPresent(runId -> {
					runningJobs.remove(runId);
					job.getSimulationFile().delete();
					job.getResultFile().delete();
					runService.endRun(runId, job);
				});
	}
}
