<#import 'macros/layout.macro.ftl' as layout/>
<#import 'macros/run/run-list.macro.ftl' as run_list>
<#import 'macros/run/run-modal-new.macro.ftl' as run_modal_new>
<#import 'macros/script/script-list.macro.ftl' as script_list>
<#import 'macros/script/script-modal-new.macro.ftl' as script_modal_new>
<#import "spring.ftl" as spring />

<@layout.main title="Project detail" menu="projects">
    <div class="page-header page-title">
        <strong>Project </strong><span>${project.name}</span>
    </div>
    <div>
        <legend style="line-height: 40px">Last runs (${runs?size})
        	<div style="float: right">
        		<a data-toggle="modal" href="#run-modal-new" class="btn btn-primary">New run</a>
        	</div>
        </legend>
        <@run_modal_new.main/>
        <@run_list.main/>
    </div>
    <div>
        <legend style="line-height: 40px">Scripts (${project.scripts?size})
        	<div style="float: right">
        		<a data-toggle="modal" href="#script-modal-new" class="btn btn-primary">New Script</a>
        	</div>
        </legend>
        <@script_modal_new.main/>
        <@script_list.main project.scripts/>
    </div>
    
</@layout.main>