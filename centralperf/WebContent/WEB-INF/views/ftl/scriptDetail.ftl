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
        <strong><a href="${rc.contextPath}/project/${project.id?c}/detail">${project.name}</a> 
        > Script </strong><a href="#" id="scriptLabelEditable" data-name="label" data-type="text" data-url="${rc.contextPath}/script/${script.id?c}" data-title="Enter script label">${script.label}</a>
		<span style="float: right">
        	<a data-toggle="modal" href="#scriptVersion-modal-new" class="btn btn-primary">New version</a>
        </span>
    </div>
    <div class="page-section">
    	Type : ${script.samplerUID}<br/>
		<a href="#" id="scriptDescriptionEditable" data-name="description" data-type="textarea" data-url="${rc.contextPath}/script/${script.id?c}" 
			data-title="Enter script description"
			data-emptyText="Click to add a description to your script">${script.description!}</a>
	</div>
	
	<div class="panel-group" id="accordion">
	
	<#-- display each version details -->
    <#list scriptVersions as version>
        <div class="panel panel-default">
        	<div class="panel-heading clearfix" >
	        	<a href="#" id="scriptVersionDescriptionEditable${version.id?c}" class="pull-left" data-name="description" data-type="text" data-url="${rc.contextPath}/script/${script.id?c}/version/${version.id?c}/" 
			data-title="Edit version description"
			data-emptyText="Click to edit the description for this version">${version.description}</a>	        	
				<div class="pull-right">
					<a data-toggle="collapse" data-parent="#accordion" href="#collapse${version.id?c}" class="btn" >
		        		<span class="caret" style=""></span>
		        	</a>				
					<a href="${rc.contextPath}/project/${project.id?c}/script/${script.id?c}/version/${version.id?c}/download" class="btn btn-success" title="Download">
		        		<span class="glyphicon glyphicon-cloud-download"></span>
		        	</a>        	
	        		<#if version_index lt scriptVersions?size -1 || scriptVersions?size gt 1 >
		        		<a href="${rc.contextPath}/project/${project.id?c}/script/${script.id?c}/version/${version.id?c}/delete" class="btn btn-danger" title="Delete version">
		        			<span class="glyphicon glyphicon-trash"></span>
		        		</a>        	
	        		</#if>
		        </div>
        	 </div>
        	<div id="collapse${version.id?c}" class="panel-collapse collapse ${(version_index == 0)?string('in','')}">
        		 <div class="panel-body">
		     	   	<h2><small>Content</small></h2>
		        	<#-- TODO : move this part to the Sampler API -->
		        	<#if script.samplerUID = "JMETER_LOCAL">
		        	<#-- additionnal div is necessary cause of a jstree bug -->
		        	<div>
		        		<div id="JMXTreeView${version.id?c}"/>
			        	<script>
			        		$('#collapse${version.id?c}').on('shown.bs.collapse', function () {
				        		$('#JMXTreeView${version.id?c}').load('${rc.contextPath}/script/${script.id?c}/version/${version.number}/preview');
							})
							if(${(version_index == 0)?string})
								$('#JMXTreeView${version.id?c}').load('${rc.contextPath}/script/${script.id?c}/version/${version.number}/preview');
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