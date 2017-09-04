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
<#import 'macros/layout.macro.ftl' as layout/>
<#import 'macros/run/run-list.macro.ftl' as run_list>
<#import 'macros/run/run-modal-new.macro.ftl' as run_modal_new>
<#import 'macros/script/script-list.macro.ftl' as script_list>
<#import 'macros/script/script-modal-new.macro.ftl' as script_modal_new>
<#import "spring.ftl" as spring />

<@layout.main title="Project detail" menu="projects">
    <div class="page-header page-title">
        <strong>Project </strong>
        <a href="#" id="projectNameEditable" data-name="name" data-type="text" data-url="${rc.contextPath}/project/${project.id}" data-title="Enter project name">${project.name}</a>
    </div>
    <div class="page-section">
    		<a href="#" id="projectDescriptionEditable" data-name="description" data-type="textarea" data-url="${rc.contextPath}/project/${project.id}" data-title="Enter project description"
    		data-placement="bottom" data-emptyText="Click to add a description to your project"
    		>${project.description!}</a>
    </div>
    <div class="page-section">
        <legend style="line-height: 40px">Last runs <span class="badge">${runs?size}</span>
        	<#if project.scripts?size gt 0>
        	<span style="float: right">
        		<a data-toggle="modal" href="#run-modal-new" class="btn btn-primary">New run</a><small>&nbsp;</small><a data-toggle="modal" href="#run-modal-import" class="btn btn-primary" projectId="${project.id}">Import run</a>
        	</span>
        	</#if>
        </legend>
        <#if project.scripts?size gt 0>
        	<@run_modal_new.main/>
        	<@run_modal_new.main action="import"/>
        </#if>
        <@run_list.main displayProject=false/>
    </div>
    <div class="page-section">
        <legend style="line-height: 40px">Scripts <span class="badge">${project.scripts?size}</span>
        	<div style="float: right">
        		<a data-toggle="modal" href="#script-modal-new" class="btn btn-primary">New Script</a>
        	</div>
        </legend>
        <@script_modal_new.main/>
        <@script_list.main project.scripts/>
    </div>
    
</@layout.main>
