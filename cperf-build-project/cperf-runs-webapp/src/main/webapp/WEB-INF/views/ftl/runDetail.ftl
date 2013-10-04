<#import 'macros/layout.macro.ftl' as layout>
<#import "spring.ftl" as spring />

<script type="text/javascript">

	// Live update of a variable value
	function updateRunVariable(inputRef){
		$.ajax({
		  type: "POST",
		  url: "${rc.contextPath}/run/${run.id}/variables/update",
		  data: {
		  		name : $(inputRef).attr('name'),
		  		value : $(inputRef).val()
		  	}
		});
	}
</script>

<@layout.main title="Run detail" menu="runs">
    <div class="page-header">
        <div>
	        <span style="float: left"><H1 >Run <strong>${run.label}</strong></H1></span>(script : <a href="${rc.contextPath}/script/${run.script.id}/detail">${run.script.label})</a>
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
                <a href="${rc.contextPath}/run/${run.id}/launch" class="btn btn-success">
                    <#if run.launched><li class="icon-forward icon-white"></li> launch again
                    <#else><li class="icon-play icon-white"></li> launch</#if>
                </a>
            </#if>
        </div>
    </div>

    <div>

    </div>

    <div style="clear:both">
    <legend>Outputs</legend>
    <ul>
            <#if run.running>
                <!-- Refreshing output -->
                <script type="text/javascript">
                    var running = true;
                    function refreshOuput() {
                      $.ajax({
                        type: "GET",
                        url: "${rc.contextPath}/run/${run.id}/output",
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
                <li>Run output : <div id="runOuput" class="scroll scroll-expanded">${run.processOutput!}</div>
                <li>Data:
                <div id="runResultCSV" class="scroll scroll-expanded">
                <ul>
                    <#list run.samples as sample>
                        <li>${sample}
                    </#list>
                </ul>
                </div>
                <li>

                <li>Number of samples : <span id="numberOfSamples">${(run.samples?size)!}</span>


                <li>Last sample : <span id="lastSampleDate">${(runSummary.lastSampleDate?datetime)!}</span>
            </#if>
        </ul>
    </div>
</@layout.main>