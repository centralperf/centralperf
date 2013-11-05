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
    
        <div id="scriptVariables" <#if collapsed>style="display:none"</#if> class="row">
        <#list script.scriptVariableSets as variableSet>
            <div class="col-md-6">
                <legend>${variableSet.name}</legend>
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
                                <input type="text" name="${variable.name}" value="${variableValue}" onchange="updateRunVariable(this)" class="form-control"/>
                            </#if>
                            <#if readonly || isCustom>
                                <span class="input-group-addon">${readonly?string(variableValue,variable.defaultValue)}</span>
                            </#if>
                        </div>
                    </div>
                </#list>
                </form>
            </div>
        </#list>
        </div>

</#macro>