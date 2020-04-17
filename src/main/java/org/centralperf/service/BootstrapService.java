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

import org.centralperf.model.Configuration;
import org.centralperf.model.dao.Project;
import org.centralperf.model.dao.Run;
import org.centralperf.sampler.driver.jmeter.JMeterSampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StreamUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * Allows to create sample data to initialize CentralPerf
 *
 * @since 1.0
 */
@Service
public class BootstrapService implements InitializingBean {

    @Resource
    private ConfigurationService configurationService;

    @Resource
    private ScriptService scriptService;

    @Resource
    private ProjectService projectService;

    @Resource
    private RunService runService;

    @Resource
    private ScriptLauncherService scriptLauncherService;

    @Value("${jmeter.sample-file}")
    org.springframework.core.io.Resource sampleJMXFile;

    @Value("${gatling.sample-file}")
    org.springframework.core.io.Resource sampleGatlingFile;

    private static final Logger log = LoggerFactory.getLogger(BootstrapService.class);

    /**
     * Check if it's necessary to launch bootstrap.
     * If the user has already imported sample data or refused it, then it returns false
     *
     * @return true if already initialized, false otherwise
     */
    public boolean isAlreadyInitialized() {
        return Boolean.parseBoolean(configurationService.getConfigurationValue(Configuration.INITIALIZED));
    }

    public void setInitialized() {
        // Update configuration, set as initialized
        configurationService.updateConfigurationValue(Configuration.INITIALIZED, Boolean.TRUE.toString());
    }

    public void importSamples() throws IOException {

        // Create sample Projet
        Project sampleProject = new Project();
        sampleProject.setName("Sample project");
        sampleProject.setDescription("Central Perf default sample Project");
        projectService.addProject(sampleProject);

        // Associate sample script
        // Load sample JMX
        String jmxContent;
        jmxContent = new Scanner(StreamUtils.copyToString(sampleJMXFile.getInputStream(), StandardCharsets.UTF_8)).useDelimiter("\\Z").next();
        scriptService.addScript(sampleProject, JMeterSampler.UID, "JMETER Sample script", "Central Perf sample JMETER script. Queries a single URL with few scenario's parameters", jmxContent);
        // Import sample resuts
        // TODO : Import sample result
    }

    @Autowired
    @Qualifier("transactionManager")
    protected PlatformTransactionManager txManager;

    /**
     * Relaunch scheduled runs
     */
    private void resumeScheduleRuns() {
        List<Run> scheduledRuns = runService.getScheduleRuns();
        log.info("Resuming scheduled runs : {} scheduled runs to resume", scheduledRuns.size());
        scheduledRuns.forEach(run -> {
            scriptLauncherService.launchSchedule(run);
            log.info("Scheduled run resumed : ID='{}', Label='{}'", run.getId(), run.getLabel());
        });
    }

    /**
     * Check if jobs where running while the application stopped (after a crash for example ...)
     * These runs should not be running on startup
     */
    private void fixIncompleteRuns() {
        List<Run> incompleteRuns = runService.getRunningRuns();
        log.info("Fixing incomplete runs : {} incomplete runs found", incompleteRuns.size());
        incompleteRuns.forEach(incompleteRun -> {
            scriptLauncherService.fixIncompleteRun(incompleteRun);
        });
    }

    /**
     * Launched after container started
     */
    @Override
    public void afterPropertiesSet() {
        log.debug("Launch bootstrap actions");

        TransactionTemplate tmpl = new TransactionTemplate(txManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                fixIncompleteRuns();
                resumeScheduleRuns();
            }
        });
    }

}
