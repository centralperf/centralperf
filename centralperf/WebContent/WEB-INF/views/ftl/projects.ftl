<#import 'macros/layout.macro.ftl' as layout/>
<#import 'macros/project/project-list.macro.ftl' as project_list/>
<#import "spring.ftl" as spring />

<@layout.main title="Projects" menu="projects">
    <legend>Projects (${projects?size})<div style="float: right"><a href="#" id="newProject" class="btn btn-primary">New project</a></div></legend>
    <@project_list.main/>
</@layout.main>