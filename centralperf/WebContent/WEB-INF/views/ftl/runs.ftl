<#import 'macros/layout.macro.ftl' as layout>
<#import 'macros/run/run-list.macro.ftl' as display_runs>
<#import "spring.ftl" as spring />

<@layout.main title="Runs" menu="runs">
    <legend>All runs <span class="badge">${runs?size}</span><div style="float: right"><a id="newRun" href="#" class="btn btn-primary">New run</a></div></legend>
    <@display_runs.main/>
</@layout.main>