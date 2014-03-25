<#--
  Copyright (C) 2014  The Central Perf authors
 
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as
  published by the Free Software Foundation, either version 3 of the
  License, or (at your option) any later version.
 
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.
 
  You should have received a copy of the GNU Affero General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->
<#macro main script readonly collapsed customScriptVariables=[]>
	<h2 id="showHideVariables" style="cursor: hand"><small>Script variables</small></h2>
    <script type="text/javascript">
        $('#showHideVariables').click(
                function(){
                    $('#scriptVariables').toggle('slow');
                    return false;
                }
        );
    </script>
    
        <div id="scriptVariables" <#if collapsed>style="display:none"</#if> class="row">
        <#list script.scriptVariableSets as variableSet>
            <div class="col-md-6">
            	<legend style="text-align:center">${variableSet.name}</legend>
                <form class="form-horizontal" role="form">
                <#list variableSet.scriptVariables as variable>
                    <div class="form-group row">
                        <label class="col-md-6 control-label" for="${variable.name}" title="${variable.description!}">${variable.name}</label>
                        <#assign isCustom = false>
                        <#assign variableName = variable.name>
                        <#assign variableNameEscaped = variableName?replace(".","\\.")?js_string>
                        <#assign variableValue = variable.defaultValue>
                        <#assign variableDefaultValueEscaped = variable.defaultValue?js_string>
                        <#list customScriptVariables as customScriptVariable>
                            <#if customScriptVariable.name == variable.name>
                                <#assign variableValue = customScriptVariable.value>
                                <#assign isCustom = true>
                            </#if>
                        </#list>
                        <div class="col-md-6 input-group">
                            <#if !readonly>
                                <input type="text" name="${variable.name}" value="${variableValue}" onchange="updateRunVariable(this,'${variableValue}')" class="form-control"/>
                            </#if>
                            <#if isCustom>
                                <span class="input-group-addon">${variableValue}</span>
                            </#if>
                            <#if readonly>
                            	<input type="text" value="${variable.defaultValue}" class="form-control" disabled title="${variable.defaultValue}"/>
                            </#if>
                        </div>
                    </div>
                </#list>
                </form>
            </div>
        </#list>
        </div>

</#macro>