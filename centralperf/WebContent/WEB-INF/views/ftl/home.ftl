<#--
  Copyright (C) 2014  The Central Perf authors
 
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as
  published by the Free Software Foundation, either version 3 of the
  License, or (at your option) any later version.
 
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.
 
  You should have received a copy of the GNU Affero General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->

<#import 'macros/layout.macro.ftl' as layout>
<#import 'macros/run/run-list.macro.ftl' as run_list>
<#import 'macros/project/project-list.macro.ftl' as project_list/>
<#import 'macros/run/run-modal-new-noproject.macro.ftl' as run_modal_new_noproject>
<#import 'macros/project/project-modal-new.macro.ftl' as project_modal_new/>
<#import "spring.ftl" as spring />

<@layout.main title="Home" menu="home">
    <div class="jumbotron home-background">
        <div>
        	<a data-toggle="modal" href="#run-modal-new" class="btn btn-primary" style="float: right;margin-top: 60px">New run</a>
        	<@run_modal_new_noproject.main/>
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
    <div>
        <legend>Last runs</legend>
        <@run_list.main/>
    </div>
    <div>
        <div class="section-title">Projects <span class="badge">${projects?size}</span><div style="float: right"><a data-toggle="modal" href="#project-modal-new" class="btn btn-primary">New project</a></div></div>
        <@project_modal_new.main/>
        <@project_list.main/>
    </div>
</@layout.main>