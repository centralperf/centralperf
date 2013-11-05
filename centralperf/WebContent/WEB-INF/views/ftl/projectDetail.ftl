<#import 'macros/layout.macro.ftl' as layout/>
<#import 'macros/run/run-list.macro.ftl' as run_list>
<#import 'macros/run/run-modal-new.macro.ftl' as run_modal_new>
<#import 'macros/script/script-list.macro.ftl' as script_list>
<#import 'macros/script/script-modal-new.macro.ftl' as script_modal_new>
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
        <legend style="line-height: 40px">Last runs <span class="badge">${runs?size}</span>
        	<div style="float: right">
        		<a data-toggle="modal" href="#run-modal-new" class="btn btn-primary">New run</a>
        		<a data-toggle="modal" href="#run-modal-import" class="btn btn-primary" projectId="${project.id}">Import run</a>
        	</div>
        </legend>
        <@run_modal_new.main/>
        <@run_modal_new.main action="import"/>
        <@run_list.main/>
    </div>
    <div>
        <legend style="line-height: 40px">Scripts <span class="badge">${project.scripts?size}</span>
        	<div style="float: right">
        		<a data-toggle="modal" href="#script-modal-new" class="btn btn-primary">New Script</a>
        	</div>
        </legend>
        <@script_modal_new.main/>
        <@script_list.main project.scripts/>
    </div>
    
</@layout.main>
