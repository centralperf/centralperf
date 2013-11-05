<#import 'macros/layout.macro.ftl' as layout>
<#import "spring.ftl" as spring />

<@layout.main title="Initialize" menu="bootstrap">
    <div class="page-header">
        <h1>Initialize Central Perf</h1>
    </div>
    <div class="container">
        It seem's that this is the first time you launched Central Perf<br/>
        Do you wish to initialize it with sample data ?<br>
        <a href="${rc.contextPath}/bootstrap/initialize?importSamples=true" class="btn">Yes</a> 
        <a href="${rc.contextPath}/bootstrap/initialize?importSamples=false" class="btn">No</a>
    </div>
</@layout.main>