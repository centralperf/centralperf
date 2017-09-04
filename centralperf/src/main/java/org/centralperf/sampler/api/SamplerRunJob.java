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

import java.io.File;

/**
 * The Run job is processing all informations during a stress test.
 * It's possible to stop the process, get the output, get the content of the file used for stress test, and the result file
 * @since 1.0
 */
public interface SamplerRunJob extends Runnable{
	
	/**
	 * Standard output for this sampler 
	 * @return	The raw output of current running job
	 */
	public String getProcessOutput();
	
	/**
	 * Stop the job and all underlying processes
	 */
	public void stopProcess();
	
	/**
	 * Get the file used (script file) for stress test
	 * @return The pointer to the file
	 */
	public File getSimulationFile();
	
	/**
	 * Get the result file, that will logically be filled during the test by the sampler
	 * @return The pointer to the result file
	 */
	public File getResultFile();
}
