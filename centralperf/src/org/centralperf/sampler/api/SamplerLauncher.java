package org.centralperf.sampler.api;

import org.centralperf.model.Run;

public interface SamplerLauncher {
	public abstract SamplerRunJob launch(String ScriptContent, Run run);
}
