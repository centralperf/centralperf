<#import 'macros/layout.macro.ftl' as layout/>
<#import 'macros/run/run-list.macro.ftl' as run_list>
<#import 'macros/script/script-list.macro.ftl' as script_list>
<#import "spring.ftl" as spring />

<@layout.main title="Project detail" menu="projects">
    <div class="page-header page-title">
        <strong>Project </strong>
        <span class="projectNameEditable" entityId="${project.id}">${project.name}</span>
    </div>
    <div class="container">
    	<#if project.description?exists>
    		<div class="projectDescriptionEditable" entityId="${project.id}" >${project.description}</div>
    	<#else>
    		<div class="projectDescriptionEditable muted" entityId="${project.id}" ondblclick="if($(this).hasClass('muted')) $(this).text(' ');$(this).removeClass('muted');">Double click to add a description to your project</div>
    	</#if>
    </div>
    <div>
        <legend>Last runs <span class="badge">${runs?size}</span>
            <div style="float: right">
                <a href="#" id="newRun" class="btn btn-primary" projectId="${project.id}">New run</a>
                <a href="#" id="importRun" class="btn btn-primary" projectId="${project.id}">Import run</a>
            </div>
        </legend>
        <@run_list.main/>
    </div>
    <div>
        <legend>Scripts <span class="badge">${project.scripts?size}</span><div style="float: right"><a href="#" id="newScript" class="btn btn-primary" projectId="${project.id}">New script</a></div></legend>
        <@script_list.main project.scripts/>
    </div>
</@layout.main>