package org.centralperf.sampler.api;

import java.io.File;

public interface SamplerRunJob extends Runnable{
	public String getProcessOutput();
	public File getJmxFile();
	public File getResultFile();
}
