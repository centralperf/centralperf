package org.centralperf.sampler.driver.gatling;

import javax.annotation.Resource;

import org.centralperf.sampler.api.Sampler;
import org.centralperf.sampler.api.SamplerLauncher;
import org.centralperf.sampler.api.SamplerScriptProcessor;
import org.springframework.stereotype.Component;

@Component
public class GatlingSampler implements Sampler {

	@Resource
	private GatlingLauncher launcher;
	
	@Resource
	private GatlingScriptProcessor scriptProcessor;
	
	public static final String UID = "GATLING_1_X";
	
	@Override
	public String getName() {
		return "Gatling 1.x";
	}

	@Override
	public String getUID() {
		return UID;
	}

	@Override
	public SamplerLauncher getLauncher() {
		return launcher;
	}

	@Override
	public SamplerScriptProcessor getScriptProcessor() {
		return scriptProcessor;
	}

}
