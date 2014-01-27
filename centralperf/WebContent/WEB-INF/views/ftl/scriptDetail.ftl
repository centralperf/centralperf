<#import 'macros/layout.macro.ftl' as layout>
<#import 'macros/script/script-variables.macro.ftl' as script_variable>
<#import "spring.ftl" as spring />

<@layout.head>
	<script type="text/javascript" src="${rc.contextPath}/resources/js/jstree/jstree.min.js"></script>
</@layout.head>

<@layout.main title="Script detail" menu="scripts">
    <div class="page-header page-title">
        <strong><a href="${rc.contextPath}/project/${project.id}/detail">${project.name}</a> 
        > Script </strong><a href="#" id="scriptLabelEditable" data-name="label" data-type="text" data-url="${rc.contextPath}/script/${script.id}" data-title="Enter script label">${script.label}</a>
    </div>
    <div class="page-section">
    	Type : ${script.samplerUID}<br/>
		<a href="#" id="scriptDescriptionEditable" data-name="description" data-type="textarea" data-url="${rc.contextPath}/script/${script.id}" 
			data-title="Enter script description"
			data-emptyText="Click to add a description to your script">${script.description!}</a>
	</div>
	
    <#list script.versions as version>
        <div class="page-section">
        	<legend style="line-height: 40px">Version ${version.number} (${version.description})
				<span style="float: right">
        			<a data-toggle="modal" href="#script-modal-new-version" class="btn btn-primary">New version</a>
        		</span>
        	</legend>
        	<h2><small>Content</small></h2>
        	<#-- TODO : move this part to the Sampler API -->
        	<#if script.samplerUID = "JMETER_LOCAL">
        	<#-- additionnal div is necessary cause of a jstree bug -->
        	<div>
        		<div id="JMXTreeView"/>
	        	<script>
	        		$('#JMXTreeView').load('${rc.contextPath}/script/${script.id}/version/${version.number}/preview');
	        	</script>        	
        	</div>
        	<#elseif script.samplerUID = "GATLING_1_X">
        		<pre>${version.content}</pre>
        	</#if>
			<@script_variable.main version true false/>
        </div>
    </#list>
</@layout.main>