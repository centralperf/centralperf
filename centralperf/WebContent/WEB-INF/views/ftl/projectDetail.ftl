<#import 'macros/layout.macro.ftl' as layout/>
<#import 'macros/run/run-list.macro.ftl' as run_list>
<#import 'macros/script/script-list.macro.ftl' as script_list>
<#import "spring.ftl" as spring />

<@layout.main title="Project detail" menu="projects">
    <div class="page-header page-title">
        <strong>Project </strong><span>${project.name}</span>
    </div>
    <div>
        <legend>Last runs (${runs?size})
            <div style="float: right">
                <a href="#" id="newRun" class="btn btn-primary" projectId="${project.id}">New run</a>
                <a href="#" id="importRun" class="btn btn-primary" projectId="${project.id}">Import run</a>
            </div>
        </legend>
        <@run_list.main/>
    </div>
    <div>
        <legend>Scripts (${project.scripts?size})<div style="float: right"><a href="#" id="newScript" class="btn btn-primary" projectId="${project.id}">New script</a></div></legend>
        <@script_list.main project.scripts/>
    </div>
</@layout.main>