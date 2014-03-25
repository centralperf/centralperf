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

package org.centralperf.sampler.driver.gatling;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.centralperf.model.dao.Run;
import org.centralperf.sampler.api.SamplerLauncher;
import org.centralperf.sampler.api.SamplerRunJob;
import org.centralperf.service.RunResultService;
import org.centralperf.service.ScriptLauncherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Gatling based Launcher
 * @see SamplerLauncher
 */
@Component
public class GatlingLauncher implements SamplerLauncher{
	
	@Value("#{appProperties['gatling.launcher.path']}")
	private String gatlingLauncherPath;
	
	@Value("#{appProperties['gatling.launcher.script.relativepath']}")
	private String gatlingLauncherScriptRelativePath;	
	
	@Value("#{appProperties['gatling.launcher.output.format']}")
	private String gatlingLauncherOutputFormat;	
	
	@Resource
	private ScriptLauncherService scriptLauncherService;

	@Resource
	private RunResultService runResultService;
	
	public static final String OUTPUT_PATTERN_PATH = "centralperf_gatling_output";
	
	/**
	 * @see SamplerLauncher
	 */
	public SamplerRunJob launch(String simulation, Run run) {
		
		String fs = System.getProperty("file.separator");
		
		// Create temporary Gatling files
		String centralPerfDirectoryPath = System.getProperty("java.io.tmpdir") + fs + "centralperf";
		String simulationDirectoryPath = centralPerfDirectoryPath  + fs + "simulations";
		String outputDirectoryPath = centralPerfDirectoryPath + fs + "output" + UUID.randomUUID();
		
		// Create folders for output
		new File(outputDirectoryPath).mkdirs();
		
		// Get class name and package name
		Pattern packagePattern = Pattern.compile("package (.*)");
		Matcher packageMatcher = packagePattern.matcher(simulation);
		String packageName = packageMatcher.find() ? packageMatcher.group(1): null;
		Pattern classnamePattern = Pattern.compile("class (.*) extends.*");
		Matcher classnameMatcher = classnamePattern.matcher(simulation);
		String classname = classnameMatcher.find() ? classnameMatcher.group(1): null;
		
		if(packageName == null || classname == null){
			// TODO : manage this error
			return null;
		}
		
		// Create folder for package
		String simulationDirectoryPackagePath = simulationDirectoryPath + fs + packageName.replaceAll("\\\\.", Matcher.quoteReplacement(fs));
		new File(simulationDirectoryPackagePath).mkdirs();
		String simulationFileName = classname + ".scala";
		String simulationFilePath = simulationDirectoryPackagePath + fs + simulationFileName;		
		
		// Put the simulation
		File simulationFile = new File(simulationFilePath);
		try {
			FileUtils.writeStringToFile(simulationFile, simulation);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// MEMO
		/*Usage: gatling [options]

				  -nr | --no-reports
				        Runs simulation but does not generate reports
				  -ro <directoryName> | --reports-only <directoryName>
				        Generates the reports for the simulation in <directoryName>
				  -df <directoryPath> | --data-folder <directoryPath>
				        Uses <directoryPath> as the absolute path of the directory where feeders are stored
				  -rf <directoryPath> | --results-folder <directoryPath>
				        Uses <directoryPath> as the absolute path of the directory where results are stored
				  -bf <directoryPath> | --request-bodies-folder <directoryPath>
				        Uses <directoryPath> as the absolute path of the directory where request bodies are stored
				  -sf <directoryPath> | --simulations-folder <directoryPath>
				        Uses <directoryPath> to discover simulations that could be run
				  -sbf <directoryPath> | --simulations-binaries-folder <directoryPath>
				        Uses <directoryPath> to discover already compiled simulations
				  -s <className> | --simulation <className>
				        Runs <className> simulation
				  -on <name> | --output-name <name>
				        Use <name> for the base name of the output directory
				  -sd <description> | --simulation-description <description>
				        A short <description> of the run to include in the report
		*/
		
		String[] command = new String[] {
				gatlingLauncherPath + fs + gatlingLauncherScriptRelativePath,
				"-sf",
				simulationDirectoryPath,
				"-s",
				packageName + "." + classname,
				"-nr",
				"-rf",
				outputDirectoryPath,
				"-on",
				OUTPUT_PATTERN_PATH				
				};
		GatlingRunJob job = new GatlingRunJob(command, run);
		job.setScriptLauncherService(scriptLauncherService);
		job.setRunResultService(runResultService);
		job.setSimulationFile(simulationFile);
		job.setResultFile(new File(outputDirectoryPath));
		job.setGatlingLauncherPath(gatlingLauncherPath);
		Thread jobThread = new Thread(job);
		jobThread.start();
		
		return job;
	}
}
