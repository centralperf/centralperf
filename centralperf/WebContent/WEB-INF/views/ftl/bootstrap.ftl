<#import 'macros/layout.macro.ftl' as layout>
<#import "spring.ftl" as spring />

<@layout.main title="Initialize" menu="bootstrap">
    <div class="page-header">
        <h1>Initialize Central Perf</h1>
    </div>
    <div class="jumbotron">
        It seem's that this is the first time you launched Central Perf<br/>
        Do you wish to initialize it with sample data ?<br>
        <a href="${rc.contextPath}/bootstrap/initialize?importSamples=true" class="btn btn-primary">Yes, import samples</a> 
        <a href="${rc.contextPath}/bootstrap/initialize?importSamples=false" class="btn btn-default">Skip...</a>
    </div>
</@layout.main>