<#macro main>
	<legend>Graphs</legend>
	<ul class="nav nav-tabs" id="graphsTab">
  		<li class="active"><a href="#sumChart" data-toggle="tab">Summary</a></li>
  		<li><a href="#respTimeChart" data-toggle="tab">Response time</a></li>
  		<li><a href="#reqSizeChart" data-toggle="tab">Request size</a></li>
  		<li><a href="#errorChart" data-toggle="tab">Error rate</a></li>
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
		<#include "/graphs/sum.ftl">
		<div id="respTimeChart" class="tab-pane"><#include "/graphs/respTime.ftl"></div>
		<div id="reqSizeChart" class="tab-pane"><#include "/graphs/reqSize.ftl"></div>
		<div id="errorChart" class="tab-pane"><#include "/graphs/error.ftl"></div>
		<div id="runProcessOuput" class="tab-pane scroll scroll-expanded terminal"></div>
		<div id="samplesPane" class="tab-pane"><div id="samplesContent" class="scroll scroll-expanded" style="white-space:normal"></div></div>
	</div>
	<script>
		$('#graphsTab a[href="#samplesPane"]').click(function (e) {
		  console.log('Displaying samples');
		  e.preventDefault();
		  $('#samplesContent').load('${rc.contextPath}/project/${run.project.id}/run/${run.id}/samples');
		});
	</script>
</#macro>