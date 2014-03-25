/**
 * The Sampler API defines interface for any CP samplers (JMeter, Gatling...)
 * Each sampler must have
 * <ul>
 * 	<li>A definition (Sampler)
 * 	<li>A launcher (SamplerLauncher)
 * 	<li>A job supervisor (SamplerRunJob)
 * 	<li>A script processor (SamplerScriptProcessor)
 * </ul>
 * @since 1.0
 * 
 */
package org.centralperf.sampler.api;