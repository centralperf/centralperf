package org.centralperf.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import org.centralperf.service.ScriptLauncherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMeterJob implements Runnable {

	private String[] command;
	private long startTime;
	private long endTime;
	private boolean running;
	private int exitStatus;
	private StringWriter processOutputWriter = new StringWriter();
	private File jmxFile;
	private File resultFile;

	private ScriptLauncherService scriptLauncherService;

	private static final Logger log = LoggerFactory.getLogger(JMeterJob.class);

	public JMeterJob(String[] command) {
		this.command = command;
	}

	@Override
	public void run() {
		log.debug("Running a new jMeter job with command "
				+ Arrays.toString(command));
		startTime = System.currentTimeMillis();
		ProcessBuilder pb = new ProcessBuilder(command);
		pb = pb.redirectErrorStream(true);	
		Process p;
		try {
			p = pb.start();
			running = true;
		    StreamWriter ouputListener = new StreamWriter(p.getInputStream(), new PrintWriter(processOutputWriter, true));			
		    ouputListener.start();		    
			while (running) {
				try {
					p.waitFor();
					running = false;
					ouputListener.interrupt();
				} catch (InterruptedException e) {
					p.destroy();
				}
			}
			exitStatus = p.exitValue();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		endTime = System.currentTimeMillis();
		scriptLauncherService.endJob(this);
	}
	
	public String getProcessOutput() {
		return processOutputWriter.toString();
	}	
	
	class StreamWriter extends Thread {
		private InputStream in;
		private PrintWriter pw;

		StreamWriter(InputStream in, PrintWriter pw) {
			this.in = in;
			this.pw = pw;
		}

		@Override
		public void run() {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = br.readLine()) != null) {
					pw.println(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String[] getCommand() {
		return command;
	}

	public void setCommand(String[] command) {
		this.command = command;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public int getExitStatus() {
		return exitStatus;
	}

	public void setExitStatus(int exitStatus) {
		this.exitStatus = exitStatus;
	}

	public ScriptLauncherService getScriptLauncherService() {
		return scriptLauncherService;
	}

	public void setScriptLauncherService(
			ScriptLauncherService scriptLauncherService) {
		this.scriptLauncherService = scriptLauncherService;
	}

	public File getJmxFile() {
		return jmxFile;
	}

	public void setJmxFile(File jmxFile) {
		this.jmxFile = jmxFile;
	}

	public File getResultFile() {
		return resultFile;
	}

	public void setResultFile(File jtlFile) {
		this.resultFile = jtlFile;
	}
}
