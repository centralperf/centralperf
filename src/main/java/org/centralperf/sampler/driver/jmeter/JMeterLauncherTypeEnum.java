package org.centralperf.sampler.driver.jmeter;

/**
 * Type of launcher for jMeter
 * @author Charles Le Gallic
 *
 */
public enum JMeterLauncherTypeEnum {
	/**
	 * Locally installed jMeter
	 */
	STANDALONE,
	/**
	 * Local Docker Container
	 */
	DOCKER_CONTAINER
}
