package org.centralperf.service.task;

import org.centralperf.exception.ConfigurationException;
import org.centralperf.helper.BackgroundThreadSessionManager;
import org.centralperf.model.dao.Run;
import org.centralperf.service.ScriptLauncherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptLauncherTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ScriptLauncherTask.class);
    private Run run;
    private ScriptLauncherService scriptLauncherService;
    private BackgroundThreadSessionManager backgroundThreadSessionManager;

    public ScriptLauncherTask(Run run, ScriptLauncherService scriptLauncherService, BackgroundThreadSessionManager backgroundThreadSessionManager) {
        this.run = run;
        this.scriptLauncherService = scriptLauncherService;
        this.backgroundThreadSessionManager = backgroundThreadSessionManager;
    }

    @Override
    public void run() {
        try {
            backgroundThreadSessionManager.bindSession();
            this.scriptLauncherService.launchNow(this.run.getId());
        } catch (ConfigurationException e) {
            log.error(String.format("Unable to launch scheduled run with ID %s", run.getId()), e);
        } finally {
            backgroundThreadSessionManager.unbindSession();
        }
    }

}
