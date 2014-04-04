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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Jmeter based Sampler Launcher
 * @see SamplerLauncher
 */
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
	
	private static final Logger log = LoggerFactory.getLogger(JMeterLauncher.class);
	/**
	 * {@inheritDoc}<br/>
	 * The jMeter launcher launches jMeter with the following parameters
	 * <ul>
	 * 		<li><pre>-Jjmeter.save.saveservice.autoflush=true</pre>: Since JMeter 2.10 is false by default for performance reasons. But we need it to flush to allow run monitoring.
	 *		<li><pre>-Jjmeter.save.saveservice.print_field_names=true</pre> 
	 *		<li><pre>-Jjmeter.save.saveservice.assertion_results_failure_message=true</pre> 
	 *		<li><pre>-Jjmeter.save.saveservice.data_type=true</pre>
	 *		<li><pre>-Jjmeter.save.saveservice.label=true</pre>
	 *		<li><pre>-Jjmeter.save.saveservice.response_code=true</pre> 
 	 *		<li><pre>-Jjmeter.save.saveservice.response_message=true</pre> 
	 *		<li><pre>-Jjmeter.save.saveservice.successful=true</pre>
	 * 		<li><pre>-Jjmeter.save.saveservice.thread_name=true</pre> 
	 *		<li><pre>-Jjmeter.save.saveservice.time=true</pre>
	 *		<li><pre>-Jjmeter.save.saveservice.assertions=true</pre> 				
	 *		<li><pre>-Jjmeter.save.saveservice.latency=true</pre>
	 *		<li><pre>-Jjmeter.save.saveservice.bytes=true</pre>
	 *		<li><pre>-Jjmeter.save.saveservice.thread_counts=true</pre> 
	 *		<li><pre>-Jjmeter.save.saveservice.sample_count=true</pre> 
	 *		<li><pre>-Jjmeter.save.saveservice.timestamp_format=ms</pre>
	 * </ul>		
	 */
	public SamplerRunJob launch(String script, Run run) {
		
		// Create temporary JMX file
		UUID uuid = UUID.randomUUID();
		String jmxFilePath = System.getProperty("java.io.tmpdir") + uuid + ".jmx";
		String jtlFilePath = System.getProperty("java.io.tmpdir") + uuid + "." + jmeterLauncherOutputFormat;
		
		File jmxFile = new File(jmxFilePath);
		try {
			FileUtils.writeStringToFile(jmxFile, script);
		} catch (IOException e1) {
			log.error("IO Error on variable replacement:"+e1.getMessage(), e1);
		}
		
		String[] command = new String[] {
				jmeterLauncherScriptPath, 
				"-n",
				"-t",
				jmxFilePath,
				"-l",
				jtlFilePath,
				"-Jjmeter.save.saveservice.output_format=" + jmeterLauncherOutputFormat,
				"-Jjmeter.save.saveservice.autoflush=true",
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
