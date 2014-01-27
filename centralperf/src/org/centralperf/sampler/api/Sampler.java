package org.centralperf.sampler.api;


public interface Sampler {
	public String getName();
	public String getUID();
	public SamplerLauncher getLauncher();
	public SamplerScriptProcessor getScriptProcessor();
}
