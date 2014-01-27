package org.centralperf.sampler.driver.gatling;

import java.util.List;

import org.centralperf.model.dao.ScriptVariable;
import org.centralperf.model.dao.ScriptVariableSet;
import org.centralperf.sampler.api.SamplerScriptProcessor;
import org.springframework.stereotype.Component;

@Component
public class GatlingScriptProcessor implements SamplerScriptProcessor {

	@Override
	public boolean validateScript(String script) {
		return script.startsWith("package");
	}

	@Override
	public List<ScriptVariableSet> getVariableSets(String script) {
		return null;
	}

	@Override
	public String replaceVariablesInScript(String scriptContent,
			List<ScriptVariable> scriptVariables) {
		return scriptContent;
	}

}
