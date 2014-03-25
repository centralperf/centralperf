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

package org.centralperf.sampler.api;

/**
 * A Sampler defines a solution which can inject requests and generate samples.
 * It must have a launcher (to launch scripts), and a script processor (to manage its script and extract variables)
 * @since 1.0
 */
public interface Sampler {
	/**
	 * Human readable of the sampler 
	 * @return The name for this sampler
	 */
	public String getName();
	
	/**
	 * Arbitratry unique ID for this sampler. Will never be displayed, but we be used to identify the kind of sampler used for a run 
	 * @return The UID of this sampler
	 */
	public String getUID();
	
	/**
	 * Launcher for this sample
	 * @return The launched of this sampler
	 */
	public SamplerLauncher getLauncher();
	
	/**
	 * The script processor analyze script to extract variables and is used to inject custom variables values in a definitive script
	 * before being launched
	 * @return The script processor for this sampler
	 */
	public SamplerScriptProcessor getScriptProcessor();
}
