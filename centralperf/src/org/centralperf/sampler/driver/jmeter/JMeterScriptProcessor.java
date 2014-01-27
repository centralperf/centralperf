package org.centralperf.sampler.driver.jmeter;

import java.util.List;

import org.centralperf.model.dao.ScriptVariable;
import org.centralperf.model.dao.ScriptVariableSet;
import org.centralperf.sampler.api.SamplerScriptProcessor;
import org.centralperf.sampler.driver.jmeter.helper.JMXScriptVariableExtractor;
import org.springframework.stereotype.Component;

@Component
public class JMeterScriptProcessor implements SamplerScriptProcessor {

	@Override
	public boolean validateScript(String script) {
		return script.indexOf("jmeterTestPlan") != -1;
	}

	@Override
	public List<ScriptVariableSet> getVariableSets(String script) {
		return JMXScriptVariableExtractor.extractVariables(script);
	}
	
	@Override
	public String replaceVariablesInScript(String scriptContent, List<ScriptVariable> scriptVariables) {
		return JMXScriptVariableExtractor.replaceVariables(scriptContent, scriptVariables);
	}	

}
