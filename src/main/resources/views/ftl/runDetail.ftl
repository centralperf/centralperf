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
<#import 'macros/layout.macro.ftl' as layout>
<#import 'macros/run/graph-panel.macro.ftl' as graph_panel>
<#import 'macros/run/run-summary-panel.macro.ftl' as run_summary_panel>
<#import 'macros/run/run-summary-panel-kibana.macro.ftl' as run_summary_panel_kibana>
<#import 'macros/script/script-variables.macro.ftl' as script_variable>
<#import "spring.ftl" as spring />

<@layout.head>
	<script type="text/javascript">
		// Live update of a variable value
		function updateRunVariable(inputRef, defaultValue){
			$.ajax({
			  type: "POST",
			  url: "${rc.contextPath}/project/${run.project.id?c}/run/${run.id?c}/variables/update",
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
        <strong><a href="${rc.contextPath}/project/${run.project.id?c}/detail">${run.project.name}</a> ></strong>
        <a href="#" id="runLabelEditable" data-name="label" data-type="text" data-url="${rc.contextPath}/run/${run.id?c}" data-title="Enter run name">${run.label}</a>
        <small><em>
        (<a 
        	href="${rc.contextPath}/project/${run.project.id?c}/script/${run.scriptVersion.script.id?c}/detail"
        	title="type : ${run.scriptVersion.script.samplerUID}, description:${run.scriptVersion.description}"
        	>${run.scriptVersion.script.label}
        </a>)
        </em></small>
        <span class="pull-right" style="vertical-align:middle">
        <#if !run.running>
			<#if run.launched>
	            <a href="${rc.contextPath}/project/${run.project.id?c}/run/${run.id?c}/copy" class="btn btn-success">
					<span class="glyphicon glyphicon-tags"></span><b>&nbsp; copy</b>
	            </a>            
			</#if>        
            <a href="${rc.contextPath}/project/${run.project.id?c}/run/${run.id?c}/launch" class="btn btn-success">
                <#if run.launched>
                	<span class="glyphicon glyphicon-forward"></span><b> launch again</b>
                <#else>
                	<span class="glyphicon glyphicon-play"></span><b> launch</b>
                </#if>
            </a>
        <#else>
        	<span id="runningIndicator">
        		<img src="${rc.contextPath}/resources/img/lemming_running.gif" style="border: 0px" title="Running...."/>
        		<a href="${rc.contextPath}/project/${run.project.id?c}/run/${run.id?c}/stop" class="btn btn-danger">
        			<span class="glyphicon glyphicon-flash"></span><b> Abort</b>
        		</a>
        	</span>
        </#if>
        </span>
    </div>
    <#if !run.sampleDataBackendType?? || run.sampleDataBackendType.name() == "DEFAULT">
	    <#if run.launched>
		    <div id="page-section summary">
		    	<div class="clearfix"><@run_summary_panel.main run/></div>
		    </div>
	    </#if>
	    <div class="page-section">
   		<a href="#" id="runCommentEditable" data-name="comment" data-type="textarea" data-url="${rc.contextPath}/run/${run.id?c}" data-title="Enter run comment"
	   		data-placement="bottom" data-emptyText="Click to add a comment">${run.comment!}</a>
	    </div>

	    <div>
	       <#if run.launched>
	        	<@graph_panel.main />
	       </#if>
	    </div>
	<#elseif run.sampleDataBackendType.name() == "ES">
		<@run_summary_panel_kibana.main run/>
    </#if>

    <@script_variable.main run.scriptVersion run.launched false run.customScriptVariables/>
</@layout.main>
