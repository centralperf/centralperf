<#import 'macros/layout.macro.ftl' as layout>
<#import 'macros/run/run-list.macro.ftl' as run_list>
<#import 'macros/project/project-list.macro.ftl' as project_list/>
<#import "spring.ftl" as spring />

<@layout.main title="Home" menu="home">
    <div class="hero-unit">
        <div >
            <a href="#" id="newRun" class="btn btn-large btn-primary" style="float: right;margin-top: 60px">New run</a>
            <H2>Central Perf</H2>
            <H1>Metrology As A Service</H1>
            <H4>
                <#if activeRuns?size==0>No<#else>${activeRuns?size}</#if> active run(s)
                <#list activeRuns as activeRun>
                    <a href="${rc.contextPath}/project/${activeRun.project.id}/run/${activeRun.id}/detail" title="Detail">${activeRun.label}</a>
                </#list>
            </H4>
        </div>
    </div>
    <div style="clear: both">
        <legend>Last runs</legend>
        <@run_list.main/>
    </div>
    <div class="container">
        <div class="section-title">Projects<div style="float: right"><a id="newProject" href="#" class="btn btn-primary">New project</a></div></div>
        <@project_list.main/>
    </div>
</@layout.main>