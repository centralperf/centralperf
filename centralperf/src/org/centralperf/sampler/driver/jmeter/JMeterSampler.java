package org.centralperf.sampler.driver.jmeter;

import javax.annotation.Resource;

import org.centralperf.sampler.api.Sampler;
import org.centralperf.sampler.api.SamplerLauncher;
import org.centralperf.sampler.api.SamplerScriptProcessor;
import org.springframework.stereotype.Component;

@Component
public class JMeterSampler implements Sampler {

	@Resource
	private JMeterLauncher launcher;
	
	@Resource
	private JMeterScriptProcessor scriptProcessor;
	
	public static final String UID = "JMETER_LOCAL";
	
	@Override
	public String getName() {
		return "JMeter local";
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
