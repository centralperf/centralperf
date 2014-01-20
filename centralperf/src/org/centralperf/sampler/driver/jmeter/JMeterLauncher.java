package org.centralperf.sampler.driver.jmeter;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.centralperf.model.dao.Run;
import org.centralperf.sampler.api.SamplerLauncher;
import org.centralperf.sampler.api.SamplerRunJob;
import org.centralperf.service.RunResultService;
import org.centralperf.service.ScriptLauncherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JMeterLauncher implements SamplerLauncher{
	
	@Value("#{appProperties['jmeter.launcher.script.path']}")
	private String jmeterLauncherScriptPath;
	
	@Value("#{appProperties['jmeter.launcher.output.format']}")
	private String jmeterLauncherOutputFormat;	
	
	@Resource
	private ScriptLauncherService scriptLauncherService;

	@Resource
	private RunResultService runResultService;
	
	public SamplerRunJob launch(String script, Run run) {
		
		// Create temporary JMX file
		UUID uuid = UUID.randomUUID();
		String jmxFilePath = System.getProperty("java.io.tmpdir") + uuid + ".jmx";
		String jtlFilePath = System.getProperty("java.io.tmpdir") + uuid + "." + jmeterLauncherOutputFormat;
		
		File jmxFile = new File(jmxFilePath);
		try {
			FileUtils.writeStringToFile(jmxFile, script);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String[] command = new String[] {
				jmeterLauncherScriptPath, 
				"-n",
				"-t",
				jmxFilePath,
				"-l",
				jtlFilePath,
				"-Jjmeter.save.saveservice.output_format=" + jmeterLauncherOutputFormat,
				"-Jjmeter.save.saveservice.print_field_names=true",
				"-Jjmeter.save.saveservice.assertion_results_failure_message=true",
				"-Jjmeter.save.saveservice.data_type=true",
				"-Jjmeter.save.saveservice.label=true",
				"-Jjmeter.save.saveservice.response_code=true",
				"-Jjmeter.save.saveservice.response_message=true",
				"-Jjmeter.save.saveservice.successful=true",
				"-Jjmeter.save.saveservice.thread_name=true",
				"-Jjmeter.save.saveservice.time=true",
				"-Jjmeter.save.saveservice.assertions=true",				
				"-Jjmeter.save.saveservice.latency=true",
				"-Jjmeter.save.saveservice.bytes=true",
				"-Jjmeter.save.saveservice.thread_counts=true",
				"-Jjmeter.save.saveservice.sample_count=true",
				"-Jjmeter.save.saveservice.timestamp_format=ms"				
				};
		JMeterRunJob job = new JMeterRunJob(command, run);
		job.setScriptLauncherService(scriptLauncherService);
		job.setRunResultService(runResultService);
		job.setJmxFile(jmxFile);
		job.setResultFile(new File(jtlFilePath));
		Thread jobThread = new Thread(job);
		jobThread.start();
		
		return job;
	}
}
