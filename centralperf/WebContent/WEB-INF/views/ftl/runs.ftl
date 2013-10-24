<#import 'macros/layout.macro.ftl' as layout>
<#import 'macros/run/run-list.macro.ftl' as display_runs>
<#import 'macros/run/run-modal-new-noproject.macro.ftl' as run_modal_new_noproject>

<#import "spring.ftl" as spring />

<@layout.main title="Runs" menu="runs">
    <legend style="line-height: 40px">All runs <span class="badge">${runs?size}</span>
	    <div style="float: right">
	    	<a data-toggle="modal" href="#run-modal-new" class="btn btn-primary">New run</a>
	    </div>
    </legend>
	<@run_modal_new_noproject.main/>
    <@display_runs.main/>
</@layout.main>
