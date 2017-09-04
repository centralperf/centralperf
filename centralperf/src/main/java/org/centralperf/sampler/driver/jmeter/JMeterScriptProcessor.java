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

package org.centralperf.sampler.driver.jmeter;

import java.util.List;

import org.centralperf.model.dao.ScriptVariable;
import org.centralperf.model.dao.ScriptVariableSet;
import org.centralperf.sampler.api.SamplerScriptProcessor;
import org.centralperf.sampler.driver.jmeter.helper.JMXScriptVariableExtractor;
import org.springframework.stereotype.Component;

/**
 * JMeter based Script Processor
 * @see SamplerScriptProcessor
 */
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
