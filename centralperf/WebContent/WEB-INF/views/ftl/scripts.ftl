<#import 'macros/layout.macro.ftl' as layout>
<#import 'macros/script/script-list.macro.ftl' as script_list>
<#import "spring.ftl" as spring />

<@layout.main title="Scripts" menu="scripts">
    <legend style="line-height: 40px">All scripts (${scripts?size})</legend>
    <@script_list.main scripts/>
</@layout.main>