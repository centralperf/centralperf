package org.centralperf.service;


/**
 * Files for bootstrap
 * @author Charles Le Gallic
 *
 */
public class BootstrapServiceFiles {

	private org.springframework.core.io.Resource sampleJMXFile;
		
	public org.springframework.core.io.Resource getSampleJMXFile() {
		return sampleJMXFile;
	}

	public void setSampleJMXFile(org.springframework.core.io.Resource sampleJMXFile) {
		this.sampleJMXFile = sampleJMXFile;
	}
}
