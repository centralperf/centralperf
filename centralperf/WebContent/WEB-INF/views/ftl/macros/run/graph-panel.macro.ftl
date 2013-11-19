<#macro main>
	<legend>Graphs</legend>
	<ul class="nav nav-tabs">
  		<li class="active"><a href="#sumChart" data-toggle="tab">Summary</a></li>
  		<li><a href="#respTimeChart" data-toggle="tab">Response time</a></li>
  		<li><a href="#">Request size</a></li>
  		<li><a href="#">Error rate</a></li>
  		<li style="float:right;margin-left:10px;margin-right:0;" ><a href="#runOuput"  class="label label-danger"  data-toggle="tab">logs</a></li>
  		<li style="float:right;margin-left:10px;margin-right:0;" ><a href="#samples" class="label label-info" data-toggle="tab">samples (${run.samples?size})</a></li>
	</ul>
	<div class="tab-content"> 
		<#include "/graphs/sum.ftl">
		<div id="respTimeChart" class="tab-pane"><#include "/graphs/respTime.ftl"></div>
		<div id="runOuput" class="tab-pane scroll scroll-expanded terminal">${run.processOutput!}</div>
		<div id="samples" class="tab-pane">
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
                <#if !run.running><a href="${rc.contextPath}/project/${run.project.id}/run/${run.id}/results" class="btn btn-primary pull-right">Download results</a></#if>
          </div>
	</div>
</#macro>