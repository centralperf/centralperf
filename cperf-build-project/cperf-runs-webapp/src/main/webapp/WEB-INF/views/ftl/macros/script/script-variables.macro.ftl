<#macro main script readonly collapsed customScriptVariables=[]>
    <legend id="showHideVariables" style="cursor: hand">Script variables</legend>
    <script type="text/javascript">
        $('#showHideVariables').click(
                function(){
                    $('#scriptVariables').toggle('slow');
                    return false;
                }
        );
    </script>
    <form class="form-horizontal">
        <div id="scriptVariables" style="<#if collapsed>display:none</#if>">
        <#list script.scriptVariableSets as variableSet>
            <div style="float:left; padding: 10px">
                <legend>${variableSet.name}</legend>
                <#list variableSet.scriptVariables as variable>
                    <div class="control-group">
                        <label class="control-label" for="{variable.name}" title="${variable.description!}">${variable.name}</label>
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
                        <div class="controls">
                                <div  class="input-append">
                                    <#if !readonly>
                                        <input type="text" name="${variable.name}" value="${variableValue}" onchange="updateRunVariable(this)" style="height:30px"/>
                                    </#if>
                                    <#if readonly || isCustom>
                                        <span class="add-on">${readonly?string(variableValue,variable.defaultValue)}</span>
                                    </#if>
                                </div>
                        </div>
                    </div>
                </#list>
            </div>
        </#list>
        </div>
    </form>
</#macro>