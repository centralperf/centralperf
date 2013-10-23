<#import 'macros/layout.macro.ftl' as layout/>
<#import 'macros/project/project-list.macro.ftl' as project_list/>
<#import 'macros/project/project-modal-new.macro.ftl' as project_modal_new/>
<#import "spring.ftl" as spring />

<@layout.main title="Projects" menu="projects">
    <legend style="line-height: 40px">Projects (${projects?size})
    <div style="float: right">
    	<a data-toggle="modal" href="#project-modal-new" class="btn btn-primary">New project</a>
    </div>
    </legend>
	<@project_modal_new.main/>
    <@project_list.main/>
</@layout.main>