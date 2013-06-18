<#import 'macros/layout.macro.ftl' as layout>
<#import "spring.ftl" as spring />

<script type="text/javascript">

	// Live update of a variable value
	function updateRunVariable(inputId, restored){
		$.ajax({
		  type: "POST",
		  url: "${rc.contextPath}/run/${run.id}/variables/update",
		  data: {
		  		name : $('#' + inputId).attr('name'),
		  		value : $('#' + inputId).val()
		  	}
		}).then(function(data){
			if(!restored){
				$('#' + inputId + "Custom").show();
			}
		});
		return false;
	}
	
	// Restore previous variable value
	function restorePreviousValue(inputId, defaultValue){
		$('#' + inputId).val(defaultValue);
		updateRunVariable(inputId, true);
		$('#' + inputId + "Custom").hide();
		return false;
	}
</script>

<@layout.main title="Run detail">
	<H1>Run detail</H1>
	<ul>
		<li>Label : ${run.label}
		<li><a id="showHideVariables" href="#">Script variables</a>
			<script type="text/javascript">
				$('a#showHideVariables').click(
					function(){
						$('#scriptVariables').toggle('slow'); 
						return false;
					}
				);
			</script>
			<ul id="scriptVariables" style="display:none">
			<#list run.script.scriptVariableSets as variableSet>
			<li>${variableSet.name}
				<table>
				<thead>
					<tr>
						<th>Name</th>
						<th>Run value</th>
						<th>Default value</th>
						<th>Description</th>
					</tr>
				</thead>
				
				<#list variableSet.scriptVariables as variable>
					<tr>
						<td>${variable.name}</td>
						<td>
							<#assign variableName = variable.name>
							<#assign variableNameEscaped = variableName?replace(".","\\.")?js_string>
							<#assign variableValue = variable.defaultValue>
							<#assign variableDefaultValueEscaped = variable.defaultValue?js_string>
							<#list run.customScriptVariables as customScriptVariable>
								<#if customScriptVariable.name == variable.name><#assign variableValue = customScriptVariable.value></#if>
							</#list>
							<input type="text" name="${variableName}" id="${variableName}" value="${variableValue}" onchange="updateRunVariable('${variableNameEscaped}', false)"/>
							<div id="${variableName}Custom" style="display:${(variableValue != variable.defaultValue)?string("block","none")}">
								Custom, <a href="#" onclick="restorePreviousValue('${variableNameEscaped}', '${variableDefaultValueEscaped}')">restore default value</a>
							</div>
							
						</td>
						<td>${variable.defaultValue}</td>
						<td>${variable.description!}</td>
					</tr>		 
				</#list>
				</table>
			</#list>
			</ul>
		<li>Launched : ${run.launched?string}
		<li>Running : <span id="isRunning">${run.running?string}</span>
		<li>Actions
			<ul>
				<#if !run.running><li><a href="${rc.contextPath}/run/${run.id}/launch">launch <#if run.launched && !run.running>again</#if></a></#if>
				<li><a href="${rc.contextPath}/run/${run.id}/duplicate">duplicate</a>
			</ul>
		<li>Script : <a href="${rc.contextPath}/script/${run.script.id}/detail">${run.script.label}</a>
		<#if run.running>
			<!-- Refreshing output -->
			<script type="text/javascript">
				var running = true;
				function refreshOuput() {
				  $.ajax({
					type: "GET",
	    			url: "/run/${run.id}/output",
	    			success: function(data) {
	    				console.log(data);
	                    if(data.running){
	                    	$("#runOuput").html(data.jobOutput); 
	                    	$("#runResultCSV").html(data.csvresult);
	                    	$("#numberOfSamples").html(data.numberOfSamples);
	                    	$("#lastSampleDate").html(data.lastSampleDate);
	                    } else{
	                    	running = false;
	                    	$("#isRunning").html("false");
	                    }
	                },
				   	complete: function() {
				    	if(running) setTimeout(refreshOuput,3000); //now that the request is complete, do it again in 5 seconds
				   	}
				  });
				}
				jQuery(document).ready(function(){
					refreshOuput();
				});	
			</script>			
		</#if>
		<#if run.launched>
			<li><a href="#" id="showHideRunOutput">Run output</a>
			<script type="text/javascript">
				$('a#showHideRunOutput').click(function(){$('#runOuput').toggle('slow');return false;});
			</script>
			<div id="runOuput" class="scroll" style="display:none">${run.processOutput!}</div>
			<li><a href="#" id="showHideRunData">Data</a>
			<script type="text/javascript">
				$('a#showHideRunData').click(function(){$('#runResultCSV').toggle('slow');return false;});
			</script>
			<div id="runResultCSV" class="scroll" style="display:none">
			<ul>
				<#list run.samples as sample>
					<li>${sample}
				</#list>
			</ul>
			</div>
			<li>Launched at : <span id="launchDate">${run.startDate!?string}</span>
			<#if !run.running>
				<li>Ended at : ${run.endDate!?string}
				<li>Duration : ${runDurationInSeconds!} seconds
			</#if>
			<li>Number of samples : <span id="numberOfSamples">${(run.samples?size)!}</span>
			
			
			<li>Last sample : <span id="lastSampleDate">${(runSummary.lastSampleDate?datetime)!}</span>
		</#if>
	</ul>
</@layout.main>