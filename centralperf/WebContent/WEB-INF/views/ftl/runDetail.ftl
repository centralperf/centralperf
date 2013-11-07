<#import 'macros/layout.macro.ftl' as layout>
<#import 'macros/run/graph-panel.macro.ftl' as graph_panel>
<#import 'macros/run/run-summary-panel.macro.ftl' as run_summary_panel>
<#import 'macros/script/script-variables.macro.ftl' as script_variable>
<#import "spring.ftl" as spring />

<script type="text/javascript">
	// Live update of a variable value
	function updateRunVariable(inputRef, defaultValue){
		$.ajax({
		  type: "POST",
		  url: "${rc.contextPath}/project/${run.project.id}/run/${run.id}/variables/update",
		  data: {
		  		name : $(inputRef).attr('name'),
		  		value : $(inputRef).val()
		  	}
		});
		
		// display default value  if not already displayed
		if($(inputRef).next("span").length == 0){
			$(inputRef).after('<span class="input-group-addon">' + defaultValue + '</span>');
		}
	}
</script>

<@layout.main title="Run detail" menu="runs">
    <div class="page-header page-title">
        <strong><a href="${rc.contextPath}/project/${run.project.id}/detail">${run.project.name}</a> ></strong> 
        <a href="#" id="runLabelEditable" data-name="label" data-type="text" data-url="${rc.contextPath}/run/${run.id}" data-title="Enter run name">${run.label}</a>
        (script : <a href="${rc.contextPath}/project/${run.project.id}/script/${run.scriptVersion.id}/detail">${run.scriptVersion.number}</a>)
        <span class="pull-right" style="vertical-align:middle">
        <#if !run.running>
            <a href="${rc.contextPath}/project/${run.project.id}/run/${run.id}/launch" class="btn btn-success">
                <#if run.launched>
                	<span class="glyphicon glyphicon-forward"></span><b> launch again</b>
                <#else>
                	<span class="glyphicon glyphicon-play"></span><b> launch</b>
                </#if>
            </a>
        <#else>
        	<span id="runningIndicator">
        		<img src="${rc.contextPath}/resources/img/lemming_running.gif" style="border: 0px" title="Running...."/>
        	</span>
        </#if>
        </span>
    </div>          
    <#if run.launched>
	    <div id="page-section summary">
	    	<@run_summary_panel.main run/>
	    </div>
    </#if>
    <div class="page-section clearfix">
   		<a href="#" id="runCommentEditable" data-name="comment" data-type="textarea" data-url="${rc.contextPath}/run/${run.id}" data-title="Enter run comment"
   		data-placement="bottom" data-emptyText="Click to add a comment">${run.comment!}</a>
    </div>      

    <div>
            <#if run.launched>
                <!-- Refreshing output -->
                <script type="text/javascript">
                    var running = ${run.running?string};
                    function refreshOuput() {
                      $.ajax({
                        type: "GET",
                        url: "${rc.contextPath}/project/${run.project.id}/run/${run.id}/output",
                        success: function(data) {
                            console.log(data);
                            if(data.summary != null){
                                $("#runOuput").html(data.jobOutput);
                                if(running) $("#runResultCSV").html(data.csvresult);
                                $("#summaryCurrentUsers").html(data.summary.currentUsers);
                                $("#summaryNumberOfSamples").html(data.summary.numberOfSample);
                                $("#summaryTotalUsers").html(data.summary.totalUsers);
                                $("#summaryCurrentBandwith").html(data.summary.currentBandwith);
                                $("#summaryTotalBandwith").html(data.summary.totalBandwith);
                                $("#summaryAverageResponseTime").html(data.summary.averageResponseTime);
                                $("#summaryAverageLatency").html(data.summary.averageLatency);
                                $("#summaryRequestPerSeconds").html(data.summary.requestPerSeconds);
                                $("#summaryNumberOfSamples").html(data.summary.numberOfSample);
                                $("#summaryErrorRate").html(data.summary.errorRate);
                                $("#summaryLastSampleDate").html(data.summary.lastSampleDate);
                            }
                            if(data.running == false && running == true){
                            	location.reload(); 
                            }
                        },
                        complete: function() {
                            if(running) setTimeout(refreshOuput,3000);
                        }
                      });
                    }
                    jQuery(document).ready(function(){
                        refreshOuput();
                    });
                </script>
            </#if>
            <#if run.launched>
            	<@graph_panel.main />
            </#if>
        </ul>
    </div>
    <@script_variable.main run.scriptVersion run.launched false run.customScriptVariables/>
</@layout.main>
