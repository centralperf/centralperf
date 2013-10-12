<#import 'macros/layout.macro.ftl' as layout>
<#import 'macros/run/upload-results-form.macro.ftl' as upload_results_form>
<#import "spring.ftl" as spring />
<script type="text/javascript">

	// Live update of a variable value
	function updateRunVariable(inputRef){
		$.ajax({
		  type: "POST",
		  url: "${rc.contextPath}/project/${run.project.id}/run/${run.id}/variables/update",
		  data: {
		  		name : $(inputRef).attr('name'),
		  		value : $(inputRef).val()
		  	}
		});
	}
</script>

<@layout.main title="Run detail" menu="runs">



    <div class="page-header">
        <div class="page-header page-title">
	        <strong><a href="${rc.contextPath}/project/${run.project.id}/detail">${run.project.name}</a> > <strong>${run.label}</strong>
            (script : <a href="${rc.contextPath}/project/${run.project.id}/script/${run.scriptVersion.id}/detail">${run.scriptVersion.number}</a>)
        </div>
        <div style="clear: both">
            <#if run.launched>
                <#if !run.running>Launched on<#else>Started since</#if>
                <span id="launchDate">${run.startDate!?string}</span>
                <#if !run.running>
                    to ${run.endDate!?string}
                </#if>
                (${runDurationInSeconds!} seconds)
            </#if>
            <#if !run.running>
                <a href="${rc.contextPath}/project/${run.project.id}/run/${run.id}/launch" class="btn btn-success">
                    <#if run.launched><li class="icon-forward icon-white"></li> launch again
                    <#else><li class="icon-play icon-white"></li> launch</#if>
                </a>
            </#if>
        </div>
    </div>

    <div style="clear:both">
            <#if run.running>
                <!-- Refreshing output -->
                <script type="text/javascript">
                    var running = true;
                    function refreshOuput() {
                      $.ajax({
                        type: "GET",
                        url: "${rc.contextPath}/project/${run.project.id}/run/${run.id}/output",
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
            	<script type='text/javascript' src='${rc.contextPath}/resources/js/highcharts/highcharts.js'></script>
				<script type='text/javascript'><#include "graphs/sum.ftl"></script>
            	<legend>Summary</legend><div id="sumChart"></div>
            	
                <legend>Logs</legend> <div id="runOuput" class="scroll scroll-expanded terminal">${run.processOutput!}</div>
                <legend>Samples (<span id="numberOfSamples">${(run.samples?size)!}</span>) - Last sample : <span id="lastSampleDate">${(runSummary.lastSampleDate?datetime)!}</span></legend>
                <div id="runResultCSV" class="scroll scroll-expanded ${(run.samples?size gt 0)?string("","terminal")}">
                <#if run.samples?size gt 0>
                    <table class="table">
                        <tr>
                            <th>timestamp</th>
                            <th>elapsed</th>
                            <th>sampleName</th>
                            <th>returnCode</th>
                            <th>latency</th>
                            <th>sizeInOctet</th>
                            <th>assertResult</th>
                            <th>status</th>
                        </tr>
                    <#list run.samples as sample>
                        <tr>
                            <td>${sample.timestamp}</td>
                            <td>${sample.elapsed}</td>
                            <td title="${sample.sampleName}"><#if sample.sampleName?length gt 15>${sample.sampleName?substring(0,15)}...<#else>${sample.sampleName}</#if></td>
                            <td>${sample.returnCode}</td>
                            <td>${sample.latency}</td>
                            <td>${sample.sizeInOctet}</td>
                            <td>${sample.assertResult?string}</td>
                            <td>${sample.status}</td>
                        </tr>
                    </#list>
                    </table>
                </#if>
                </div>
            <#else>
                Upload your own results ?
                <div>
                    <@upload_results_form.main run/>
                </div>
            </#if>
        </ul>
    </div>
    <#import 'macros/script/script-variables.macro.ftl' as script_variable>
    <@script_variable.main run.scriptVersion run.launched false run.customScriptVariables/>
</@layout.main>