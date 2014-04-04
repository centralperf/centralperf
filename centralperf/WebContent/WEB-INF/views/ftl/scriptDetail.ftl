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
<#import 'macros/script/script-variables.macro.ftl' as script_variable>
<#import 'macros/script/scriptversion-modal-new.macro.ftl' as scriptversion_modal_new>
<#import "spring.ftl" as spring />

<@layout.head>
	<script type="text/javascript" src="${rc.contextPath}/resources/js/jstree/jstree.min.js"></script>
</@layout.head>

<@layout.main title="Script detail" menu="scripts">
    <div class="page-header page-title">
        <strong><a href="${rc.contextPath}/project/${project.id}/detail">${project.name}</a> 
        > Script </strong><a href="#" id="scriptLabelEditable" data-name="label" data-type="text" data-url="${rc.contextPath}/script/${script.id}" data-title="Enter script label">${script.label}</a>
		<span style="float: right">
        	<a data-toggle="modal" href="#scriptVersion-modal-new" class="btn btn-primary">New version</a>
        </span>
    </div>
    <div class="page-section">
    	Type : ${script.samplerUID}<br/>
		<a href="#" id="scriptDescriptionEditable" data-name="description" data-type="textarea" data-url="${rc.contextPath}/script/${script.id}" 
			data-title="Enter script description"
			data-emptyText="Click to add a description to your script">${script.description!}</a>
	</div>
	
	<div class="panel-group" id="accordion">
	
	<#-- display each version details -->
    <#list scriptVersions as version>
        <div class="panel panel-default">
        	<div class="panel-heading" style="display:table;width:100%">
        		<a data-toggle="collapse" data-parent="#accordion" href="#collapse${version.id}" lass="pull-left" style="vertical-align: middle; display: table-cell;">
	        		Version ${version.number} (${version.description})
	        	</a>
				<span class="pull-right" style="display: table-cell;">
					<a href="${rc.contextPath}/project/${project.id}/script/${script.id}/version/${version.id}/download" class="btn btn-success" title="Download">
		        		<span class="glyphicon glyphicon-cloud-download"></span>
		        	</a>        	
	        		<#if version_index lt scriptVersions?size -1 || scriptVersions?size gt 1 >
		        		<a href="${rc.contextPath}/project/${project.id}/script/${script.id}/version/${version.id}/delete" class="btn btn-danger" title="Delete version">
		        			<span class="glyphicon glyphicon-trash"></span>
		        		</a>        	
	        		</#if>
		        </span>
        	 </div>
        	<div id="collapse${version.id}" class="panel-collapse collapse ${(version_index == 0)?string('in','')}">
        		 <div class="panel-body">
		     	   	<h2><small>Content</small></h2>
		        	<#-- TODO : move this part to the Sampler API -->
		        	<#if script.samplerUID = "JMETER_LOCAL">
		        	<#-- additionnal div is necessary cause of a jstree bug -->
		        	<div>
		        		<div id="JMXTreeView${version.id}"/>
			        	<script>
			        		$('#collapse${version.id}').on('shown.bs.collapse', function () {
				        		$('#JMXTreeView${version.id}').load('${rc.contextPath}/script/${script.id}/version/${version.number}/preview');
							})
							if(${(version_index == 0)?string})
								$('#JMXTreeView${version.id}').load('${rc.contextPath}/script/${script.id}/version/${version.number}/preview');
			        	</script>        	
		        	</div>
		        	<#elseif script.samplerUID = "GATLING_1_X">
		        		<pre>${version.content}</pre>
		        	</#if>
					<@script_variable.main version true false/>
				</div>
			</div>
        </div>
    </#list>
    </div>
    <@scriptversion_modal_new.main/>
</@layout.main>