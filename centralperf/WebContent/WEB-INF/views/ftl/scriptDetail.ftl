<#import 'macros/layout.macro.ftl' as layout>
<#import 'macros/script/script-variables.macro.ftl' as script_variable>
<#import "spring.ftl" as spring />

<@layout.main title="Script detail" menu="scripts">
    <div class="page-header page-title">
        <strong><a href="${rc.contextPath}/project/${project.id}/detail">${project.name}</a> 
        > Script </strong><span class="scriptLabelEditable editableText" entityId="${script.id}">${script.label}</span>
    </div>
    ${script.description!}
    <#list script.versions as version>
        <legend><h3>Version ${version.number} (${version.description})</h3></legend>
        <legend>JMX content</legend>
        <div class="scroll scroll-collapsed"><pre>${version.jmx?xml}</pre></div></li>
        <@script_variable.main version true false/>
    </#list>
</@layout.main>