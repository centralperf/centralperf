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

package org.centralperf.sampler.api;

import java.util.List;

import org.centralperf.model.dao.ScriptVariable;
import org.centralperf.model.dao.ScriptVariableSet;

/**
 * A Processor handle all operations on script
 * @since 1.0
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
