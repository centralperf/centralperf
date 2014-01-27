package org.centralperf.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.centralperf.sampler.api.Sampler;
import org.centralperf.sampler.driver.gatling.GatlingSampler;
import org.centralperf.sampler.driver.jmeter.JMeterSampler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * Manager available sampler types (JMeter, Gatling...) 
 * @author Charles Le Gallic
 *
 */
@Service
public class SamplerService implements InitializingBean{
	
	private Map<String,Sampler> samplers;
	
	@Resource
	private GatlingSampler gatlingSampler;

	@Resource
	private JMeterSampler jMeterSampler;	
	
	public Collection<Sampler> getSamplers() {
		return samplers.values();
	}
	
	public void setSamplers(Collection<Sampler> samplers) {
		if(this.samplers != null) 
			this.samplers.clear();
		else
			 this.samplers = new HashMap<String, Sampler>();
		for (Sampler sampler : samplers) {
			this.samplers.put(sampler.getUID(), sampler);
		}
	}
	
	public Sampler getSamplerByUID(String UID){
		return samplers.get(UID);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.samplers = new HashMap<String, Sampler>();
		this.samplers.put(gatlingSampler.getUID(), gatlingSampler);
		this.samplers.put(jMeterSampler.getUID(), jMeterSampler);
	}
	
}
