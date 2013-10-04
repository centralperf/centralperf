<#import 'macros/layout.macro.ftl' as layout/>
<#import 'macros/run/run-list.macro.ftl' as run_list>
<#import 'macros/run/new-run-form.macro.ftl' as new_run_form>
<#import 'macros/script/script-list.macro.ftl' as script_list>
<#import 'macros/script/new-script-form.macro.ftl' as new_script_form>
<#import "spring.ftl" as spring />

<@layout.main title="Project detail" menu="projects">
    <div class="page-header page-title">
        <strong>Project </strong><span>${project.name}</span>
    </div>
    <div>
        <legend>Last runs<div style="float: right"><a href="#" id="newRun" class="btn btn-primary">New run</a></div></legend>
        <@run_list.main/>
    </div>
    <div>
        <legend>Scripts<div style="float: right"><a href="#" id="newScript" class="btn btn-primary">New script</a></div></legend>
        <@script_list.main/>
    </div>
</@layout.main>