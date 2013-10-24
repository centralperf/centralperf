<#import 'macros/layout.macro.ftl' as layout>
<#import "spring.ftl" as spring />

<@layout.main title="Run detail">
	<H1>Run detail</H1>
	<ul>
		<li>Label : ${run.label}
		<li>Launched : ${run.launched?string}
		<li>Running : <span id="isRunning">${run.running?string}</span>
		<#if !run.running>
			<a href="${rc.contextPath}/run/${run.id}/launch">launch <#if run.launched && !run.running>again</#if></a>
		</#if>
		<li>Script : <a href="${rc.contextPath}/script/${run.script.id}/edit">${run.script.label}</a>
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
			<li>Run output : <div id="runOuput" class="scroll">${run.processOutput!}</div>
			<li>Data: 
			<div id="runResultCSV" class="scroll">
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