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

import javax.annotation.Resource;

import org.centralperf.sampler.api.Sampler;
import org.centralperf.sampler.api.SamplerLauncher;
import org.centralperf.sampler.api.SamplerScriptProcessor;
import org.springframework.stereotype.Component;

/**
* Gatling 1.x based Sampler
* @since 1.0 
*/
@Component
public class GatlingSampler implements Sampler {

	@Resource
	private GatlingLauncher launcher;
	
	@Resource
	private GatlingScriptProcessor scriptProcessor;
	
	public static final String UID = "GATLING_1_X";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "Gatling 1.x";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUID() {
		return UID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SamplerLauncher getLauncher() {
		return launcher;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SamplerScriptProcessor getScriptProcessor() {
		return scriptProcessor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getScriptFileExtension() {
		return ".scala";
	}

}
