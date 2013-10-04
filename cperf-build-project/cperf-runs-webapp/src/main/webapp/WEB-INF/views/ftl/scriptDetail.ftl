<#import 'macros/layout.macro.ftl' as layout>
<#import 'macros/script/script-variables.macro.ftl' as script_variable>
<#import "spring.ftl" as spring />

<@layout.main title="Script detail" menu="scripts">
    <div class="page-header page-title">
        <strong>Script </strong><span>${script.label}</span>
    </div>
    <legend>JMX content</legend>
	<div class="scroll scroll-collapsed"><pre>${script.jmx?xml}</pre></div></li>
    <@script_variable.main script true false/>
</@layout.main>