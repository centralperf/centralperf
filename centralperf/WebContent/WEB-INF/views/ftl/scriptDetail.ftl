<#import 'macros/layout.macro.ftl' as layout>
<#import 'macros/script/script-variables.macro.ftl' as script_variable>
<#import "spring.ftl" as spring />

<@layout.main title="Script detail" menu="scripts">
    <div class="page-header page-title">
        <strong><a href="${rc.contextPath}/project/${project.id}/detail">${project.name}</a> 
        > Script </strong><a href="#" id="scriptLabelEditable" data-name="label" data-type="text" data-url="${rc.contextPath}/script/${script.id}" data-title="Enter script label">${script.label}</a>
    </div>
	<a href="#" id="scriptDescriptionEditable" data-name="description" data-type="textarea" data-url="${rc.contextPath}/script/${script.id}" 
	data-title="Enter script description"
	data-placement="bottom" 
	data-emptyText="Click to add a description to your script"
	>${script.description!}</a>
	
    <#list script.versions as version>
        <legend><h3>Version ${version.number} (${version.description})</h3></legend>
        <legend>JMX content</legend>
        <div class="scroll scroll-collapsed"><pre>${version.jmx?xml}</pre></div></li>
        <@script_variable.main version true false/>
    </#list>
</@layout.main>