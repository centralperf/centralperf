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

import org.centralperf.model.dao.Run;

/**
 * The sampler launcher launch internal or external tools and produce a Sampler Run Job that will be used
 * to collect samples results while running
 * @since 1.0
 */
public interface SamplerLauncher {
	
	/**
	 * Launched the sampler and provides a job to interact with it
	 * @param ScriptContent	The script content to be used
	 * @param run	The run associated with this launch
	 * @return	A running job
	 */
	public abstract SamplerRunJob launch(String ScriptContent, Run run);
}
