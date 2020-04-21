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
<#macro main run>
	<#if run.launched && run.lastStartDate??>
		<#assign refreshIntervalString>refreshInterval:('$$hashKey':'object:5393',display:'5%20seconds',pause:!f,section:${run.running?then('1','0')},value:${run.running?then('5000','0')})</#assign>
		<#assign timeString><#if run.running>time:(from:'${run.lastStartDate?iso_utc}',mode:relative,to:now)<#else>time:(from:'${run.lastStartDate?iso_utc}',mode:absolute,to:'${run.lastEndDate?iso_utc}')</#if></#assign>
		<#assign url>${kibanaUrl}/app/kibana#/dashboard/centralperf_overview?_g=(${refreshIntervalString},${timeString})&_a=(filters:!(),options:(darkTheme:!f),query:(query_string:(analyze_wildcard:!t,query:'%2BrunId:${run.id}+%2BprojectId:${run.project.id}')),timeRestore:!f,title:'CentralPerf+-+Default',uiState:(P-1:(vis:(legendOpen:!t))),viewMode:view)</#assign>

		<div class="pull-right form-group form-inline">
			<a href="${url}" class="btn btn-info" target="_blank"><span class="glyphicon glyphicon-new-window"
																		aria-hidden="true"></span> open in Kibana</a>
			<#if otherRuns?has_content>
				<a id="compareWithOtherRunBtn" class="btn btn-success">
					<span class="glyphicon glyphicon-transfer" aria-hidden="true"></span> Compare with : </a>
				<select id="compareWithRun" name="compareWithRunId" class="form-control">
					<#list otherRuns as otherRun>
						<#if otherRun.launched && otherRun.lastStartDate??>
							<option value="${otherRun.id}">${otherRun.label} (id : ${otherRun.id}
								, ${otherRun.lastStartDate?datetime?string.short})
							</option>
						</#if>
					</#list>
				</select>
				<a id="returnToCurrentRun" class="btn btn-success hidden"><span
							class="glyphicon glyphicon-triangle-left" aria-hidden="true"></span>&nbsp;</a>
			</#if>
		</div>
		<iframe
				id="kibanaIFrame"
				src="${url}&embed=true"
				class="embeddedApp"
				height="600" width="100%"></iframe>
	</#if>
	
	<script langage="javascript">
		var running = ${run.running?string};

		function checkStatus(){
			$.getJSON("${rc.contextPath}/api/run/${run.id}/status", '', function (res) {
				running = res.running;
				if (running) {
					setTimeout(checkStatus, ${refreshDelay?c});
				} else {
					document.location.href = "${rc.contextPath}/project/${run.project.id}/run/${run.id}/detail";
				}
			});
		}

		let currentRunKibanaUrl = "${url}";
		let compareUrlTemplate = "${kibanaUrl}/app/kibana#/visualize/edit/centralperf_response_time_per_sample_comparison?_g=(time:(from:'OLDEST_START_DATE',mode:relative,to:now))&_a=(filters:!(),options:(darkTheme:!f),query:(language:kuery,query:'runId+:${run.id}+or+runId+:OTHER_RUN_ID'),timeRestore:!f,title:'CentralPerf+-+Default',uiState:(P-1:(vis:(legendOpen:!t))),viewMode:view)"
		$("#compareWithOtherRunBtn").click(function () {
			let compareUrl = compareUrlTemplate
					.replace("OLDEST_START_DATE", "2000-01-01T00:00:00.000Z")
					.replace("OTHER_RUN_ID", $("#compareWithRun").val());
			$("#kibanaIFrame").prop("src", compareUrl);
			$("#returnToCurrentRun").removeClass("hidden");
		});
		$("#returnToCurrentRun").click(function () {
			$("#kibanaIFrame").prop("src", currentRunKibanaUrl);
			$("#returnToCurrentRun").addClass("hidden");
		});

		<#if run.running>
		jQuery(document).ready(
				function () {
					setTimeout(checkStatus, 1000);
				}
		);
		</#if>

	</script>	
</#macro>