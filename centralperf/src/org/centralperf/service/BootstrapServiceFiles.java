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


/**
 * Reference sample files for bootstraping
 * @since 1.0
 */
public class BootstrapServiceFiles {

	private org.springframework.core.io.Resource sampleJMXFile;
	private org.springframework.core.io.Resource sampleGatlingFile;
		
	public org.springframework.core.io.Resource getSampleJMXFile() {
		return sampleJMXFile;
	}

	public void setSampleJMXFile(org.springframework.core.io.Resource sampleJMXFile) {
		this.sampleJMXFile = sampleJMXFile;
	}

	public org.springframework.core.io.Resource getSampleGatlingFile() {
		return sampleGatlingFile;
	}

	public void setSampleGatlingFile(
			org.springframework.core.io.Resource sampleGatlingFile) {
		this.sampleGatlingFile = sampleGatlingFile;
	}
	
	
}
