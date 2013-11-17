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
		<a href="#" id="scriptDescriptionEditable" data-name="description" data-type="textarea" data-url="${rc.contextPath}/script/${script.id}" 
			data-title="Enter script description"
			data-emptyText="Click to add a description to your script">${script.description!}</a>
	</div>
	
    <#list script.versions as version>
        <div class="page-section">
        	<legend>Version ${version.number} (${version.description})</legend>
        	<h2><small>JMX content</small></h2>
        	<#-- additionnal div is necessary cause of a jstree bug -->
        	<div>
        		<div id="JMXTreeView"/>
	        	<script>
	        		$('#JMXTreeView').load('${rc.contextPath}/script/${script.id}/version/${version.number}/preview');
	        	</script>        	
        	</div>
			<@script_variable.main version true false/>
        </div>
    </#list>
</@layout.main>