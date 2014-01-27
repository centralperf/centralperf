package org.centralperf.sampler.api;

import java.util.List;

import org.centralperf.model.dao.ScriptVariable;
import org.centralperf.model.dao.ScriptVariableSet;

/**
 * A Processor handle all operations on script
 * @author Charles Le Gallic
 *
 */
public interface SamplerScriptProcessor {
	
	/**
	 * Validates the provided script is compatible with this sampler
	 * @param script
	 * @return true if script is compatible
	 */
	public boolean validateScript(String script);
	
	/**
	 * Get the variables for provided script
	 * @param script
	 * @return a list of variable sets
	 */
	public List<ScriptVariableSet> getVariableSets(String script);

	/**
	 * Returns a script with variables replaced with provided values
	 * @param scriptContent
	 * @param scriptVariables
	 * @return
	 */
	public String replaceVariablesInScript(String scriptContent,List<ScriptVariable> scriptVariables);
}
