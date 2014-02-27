<#import 'macros/layout.macro.ftl' as layout>
<#import 'macros/run/graph-panel.macro.ftl' as graph_panel>
<#import 'macros/run/run-summary-panel.macro.ftl' as run_summary_panel>
<#import 'macros/script/script-variables.macro.ftl' as script_variable>
<#import "spring.ftl" as spring />

<@layout.head>
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
</@layout.head>

<@layout.main title="Run detail" menu="runs">
    <div class="page-header page-title">
        <strong><a href="${rc.contextPath}/project/${run.project.id}/detail">${run.project.name}</a> ></strong> 
        <a href="#" id="runLabelEditable" data-name="label" data-type="text" data-url="${rc.contextPath}/run/${run.id}" data-title="Enter run name">${run.label}</a>
        <small><em>
        (<a 
        	href="${rc.contextPath}/project/${run.project.id}/script/${run.scriptVersion.script.id}/detail" 
        	title="type : ${run.scriptVersion.script.samplerUID}, description:${run.scriptVersion.description}"
        	>${run.scriptVersion.script.label}
        </a>)
        </em></small>
        <span class="pull-right" style="vertical-align:middle">
        <#if !run.running>
			<#if run.launched>
	            <a href="${rc.contextPath}/project/${run.project.id}/run/${run.id}/copy" class="btn btn-success">
					<span class="glyphicon glyphicon-tags"></span><b>&nbsp; copy</b>
	            </a>            
			</#if>        
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
        		<a href="${rc.contextPath}/project/${run.project.id}/run/${run.id}/stop" class="btn btn-danger">
        			<span class="glyphicon glyphicon-flash"></span><b> Abort</b>
        		</a>
        	</span>
        </#if>
        </span>
    </div>          
    <#if run.launched>
	    <div id="page-section summary">
	    	<div class="clearfix"><@run_summary_panel.main run/></div>
	    </div>
    </#if>
    <div class="page-section">
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
                            if(data.runDetailStatistics != null){
                                $("#runProcessOuput").html(data.jobOutput);
                                <!-- if(running) $("#runResultCSV").html(data.csvresult); -->
                                $("#summaryCurrentUsers").html(data.runDetailStatistics.currentUsers);
                                $("#summaryNumberOfSamples").html(data.runDetailStatistics.numberOfSample);
                                $("#summaryMaxUsers").html(data.runDetailStatistics.maxUsers);
                                
                                $("#summaryCurrentBandwith").html(data.runDetailStatistics.currentBandwithWithUnit);
                                $("#summaryTotalBandwith").html(data.runDetailStatistics.totalBandwithWithUnit);
                                $("#summaryAverageResponseTime").html(data.runDetailStatistics.averageResponseTime);
                                $("#summaryAverageLatency").html(data.runDetailStatistics.averageLatency);
                                $("#summaryRequestPerSeconds").html(Math.round(data.runDetailStatistics.numberOfSample / data.runDetailStatistics.duration * 100000)/100);
                                $("#summaryErrorRate").html(data.runDetailStatistics.errorRate + "%");
                                $("#summaryNumberOfSamples").html(data.runDetailStatistics.numberOfSample);
                                $("#summaryLastSampleDate").html(data.runDetailStatistics.lastSampleDate);
                                $("#summaryLaunchedTime").html("${run.startDate?time}");
                                <#if !run.running>
                                	var duration = moment.duration(data.runDetailStatistics.duration);
                                	$("#summaryDuration").html(duration.humanize());
                               	</#if>
                            }
                            if(data.runDetailGraphSum != null){
                                sChart.series[0].setData(JSON.parse(data.runDetailGraphSum.rstSerie));
                                sChart.series[1].setData(JSON.parse(data.runDetailGraphSum.reqSerie));
                            }
                            if(data.runDetailGraphRc != null){
                                sChart.series[2].data[0].update(y=data.runDetailGraphRc.http1xxRatio);
                                sChart.series[2].data[1].update(y=data.runDetailGraphRc.http2xxRatio);
                                sChart.series[2].data[2].update(y=data.runDetailGraphRc.http3xxRatio);
                                sChart.series[2].data[3].update(y=data.runDetailGraphRc.http4xxRatio);
                                sChart.series[2].data[4].update(y=data.runDetailGraphRc.http5xxRatio);
                                sChart.series[2].data[5].update(y=data.runDetailGraphRc.httpErrRatio);
                            }
                            if(data.runDetailGraphRt != null){
                            	rtChart.xAxis[0].setCategories(JSON.parse(data.runDetailGraphRt.label));
                                rtChart.series[0].setData(JSON.parse(data.runDetailGraphRt.download));
                                rtChart.series[1].setData(JSON.parse(data.runDetailGraphRt.latency));
                                rsChart.xAxis[0].setCategories(JSON.parse(data.runDetailGraphRt.label));
                                rsChart.series[0].setData(JSON.parse(data.runDetailGraphRt.size));
                            }
                            if(data.runDetailGraphError != null){
                            	errorChart.xAxis[0].setCategories(JSON.parse(data.runDetailGraphError.label));
                                errorChart.series[0].setData(JSON.parse(data.runDetailGraphError.nbKo));
                                errorChart.series[1].setData(JSON.parse(data.runDetailGraphError.nbOk));
                            }
                            if(data.running == false && running == true){
                            	location.reload(); 
                            }
                            
                        },
                        complete: function() {
                            if(running) setTimeout(refreshOuput,${refreshDelay?c});
                        }
                      });
                    }                    
                    function refreshDuration(startAt){
                    	$("#summaryDuration").html(Math.round((new Date() - startAt) / 1000) + " s");
                    	setTimeout(function(){refreshDuration(startAt);}, 1000);
                    }
                    jQuery(document).ready(function(){
                        refreshOuput();
                        <#if run.running>
							$.ajax({
                        		type: "GET",
                        		url: "${rc.contextPath}/project/${run.project.id}/run/${run.id}/startat/"+(new Date()).getTime(),
                        		success: function(data) {
                        			setTimeout(function(){refreshDuration(data);}, 1000);
                        		}
							});  							
						</#if>   
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
