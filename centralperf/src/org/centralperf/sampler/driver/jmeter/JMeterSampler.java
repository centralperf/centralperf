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

import javax.annotation.Resource;

import org.centralperf.sampler.api.Sampler;
import org.centralperf.sampler.api.SamplerLauncher;
import org.centralperf.sampler.api.SamplerScriptProcessor;
import org.springframework.stereotype.Component;

/**
 * Jmeter based Sampler
 * @see Sampler
 */
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
