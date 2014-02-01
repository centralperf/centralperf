package org.centralperf.sampler.api;

import java.io.File;

public interface SamplerRunJob extends Runnable{
	public String getProcessOutput();
	public void stopProcess();
	public File getSimulationFile();
	public File getResultFile();
}
