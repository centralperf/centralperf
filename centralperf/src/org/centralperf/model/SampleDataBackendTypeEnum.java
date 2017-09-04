package org.centralperf.model;

/**
 * Type of backend to use to store CP sample Data 
 * @author Charles Le Gallic
 *
 */
public enum SampleDataBackendTypeEnum {
	/**
	 * Central Perf database
	 */
	DEFAULT,
	/**
	 * Elastic Search cluster
	 */
	ES
}
