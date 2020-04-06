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

package org.centralperf.service;

import org.centralperf.sampler.api.Sampler;
import org.centralperf.sampler.driver.jmeter.JMeterSampler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager available sampler types (JMeter, Gatling...)
 *
 * @since 1.0
 */
@Service
public class SamplerService implements InitializingBean {

    private Map<String, Sampler> samplers;

	@Resource
	private JMeterSampler jMeterSampler;	
	
	public Collection<Sampler> getSamplers() {
		return samplers.values();
	}
	
	public void setSamplers(Collection<Sampler> samplers) {
		if(this.samplers != null) 
			this.samplers.clear();
		else
			 this.samplers = new HashMap<>();
		for (Sampler sampler : samplers) {
			this.samplers.put(sampler.getUID(), sampler);
		}
	}
	
	public Sampler getSamplerByUID(String UID){
		return samplers.get(UID);
	}

	@Override
	public void afterPropertiesSet() {
		this.samplers = new HashMap<>();
		this.samplers.put(jMeterSampler.getUID(), jMeterSampler);
	}
	
}
