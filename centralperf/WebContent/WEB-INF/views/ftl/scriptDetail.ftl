<#import 'macros/layout.macro.ftl' as layout>
<#import 'macros/script/script-variables.macro.ftl' as script_variable>
<#import "spring.ftl" as spring />

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
        	<div class="scroll scroll-collapsed"><pre>${version.jmx?xml}</pre></div></li>
        	<@script_variable.main version true false/>
        </div>
    </#list>
</@layout.main>