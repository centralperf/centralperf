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
<#macro main>

	<!-- Load css for c3 basic styles -->
	<link rel="stylesheet" type="text/css" href="${rc.contextPath}/resources/css/graphs/c3.css">
	<!-- Load d3.js and c3.js -->
	<script src="${rc.contextPath}/resources/js/graphs/d3.min.js" charset="utf-8"></script>
	<script src="${rc.contextPath}/resources/js/graphs/c3.js"></script>
	
	<legend>Graphs</legend>
	<ul class="nav nav-tabs" id="graphsTab">
  		<li class="active"><a href="#sumChart" data-toggle="tab" id="SUM">Summary</a></li>
  		<li><a href="#respTimeChart" data-toggle="tab"  id="RT">Response time</a></li>
  		<li><a href="#reqSizeChart" data-toggle="tab" id="RS">Request size</a></li>
  		<li><a href="#errorChart" data-toggle="tab" id="ER">Error rate</a></li>
  		<li style="float:right;margin-left:10px;margin-right:0;" ><a href="#runProcessOuput" class="label label-danger"  data-toggle="tab">logs</a></li>
  		<li style="float:right;margin-left:10px;margin-right:0;" ><a href="#samplesPane" class="label label-info" data-toggle="tab">samples (${run.samples?size})</a></li>
		<#if !run.running>
        <li style="float:right;margin-left:10px;margin-right:0;" class="dropdown">
			<a href="#" class="primary dropdown-toggle" data-toggle="dropdown">
				Download results<b class="caret"></b>
			</button>
			<ul class="dropdown-menu" role="menu">
				<li><a href="${rc.contextPath}/project/${run.project.id}/run/${run.id}/results">Comma separated (.csv)</a></li>
                <li><a href="${rc.contextPath}/project/${run.project.id}/run/${run.id}/centralperf.xlsx">Excel 2007+ file (.xlsx)</a></li>
			</ul>
		</#if>  		
	</ul>
	<div class="tab-content"> 
		<div id="sumChart" class="tab-pane active"><div id=summaryChart></div></div>
		<div id="respTimeChart" class="tab-pane"><div id=responseTimeChart></div></div>
		<div id="reqSizeChart" class="tab-pane"><div id=responseSizeChart></div></div>
		<div id="errorChart" class="tab-pane"><div id=errorRateChart></div></div>
		<div id="runProcessOuput" class="tab-pane scroll scroll-expanded terminal"></div>
		<div id="samplesPane" class="tab-pane"><div id="samplesContent" class="scroll scroll-expanded" style="white-space:normal"></div></div>
	</div>
	
	<#include "/graphs/c3Charts.ftl">
	<script langage="javascript">
		var running = ${run.running?string};
		var graphTab=new Array();
		var newTab="SUM";
		
		
		$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
			var oldTab=e.relatedTarget.id;
			newTab=e.target.id;
			if(graphTab[oldTab]!=null){graphTab[oldTab].hide();}
			if(graphTab[newTab]==null){graphTab[newTab]=generateGraph(newTab);}
			else{graphTab[newTab].show();}
    	});

		$('#graphsTab a[href="#samplesPane"]').click(function (e) {
		  console.log('Displaying samples');
		  e.preventDefault();
		  $('#samplesContent').load('${rc.contextPath}/project/${run.project.id}/run/${run.id}/samples');
		});
		
		function autoRefresh() {
			refreshStats();
			graphTab[newTab]=generateGraph(newTab);
			if(running) {setTimeout(function(){autoRefresh();}, ${refreshDelay?c});}
		}
		autoRefresh();
		
		function refreshDuration(startAt){
			$("#summaryDuration").html(prettyDuration(new Date() - startAt));
			setTimeout(function(){refreshDuration(startAt);}, 1000);
		}
                    
		// Display a ms duration as hh:mm:ss
		function prettyDuration(durationInMs){
			var duration = moment.duration(durationInMs);
			var h = duration.hours();
			var m = duration.minutes();
			var s = duration.seconds();
			return (h < 10 ? "0" : "") + h + ":" + (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s;
		}
                    
        <#if run.running>
			jQuery(document).ready(function(){
				$.ajax({
					type: "GET",
					url: "${rc.contextPath}/project/${run.project.id}/run/${run.id}/startat/"+(new Date()).getTime(),
					success: function(data) {
						setTimeout(function(){refreshDuration(data);}, 1000);
					}
				});  							
			});
		</#if>
		
	</script>
</#macro>