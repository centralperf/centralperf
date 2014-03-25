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
<#import 'macros/project/project-list.macro.ftl' as project_list/>
<#import 'macros/project/project-modal-new.macro.ftl' as project_modal_new/>
<#import "spring.ftl" as spring />

<@layout.main title="Projects" menu="projects">
    <legend style="line-height: 40px">Projects  <span class="badge">${projects?size}</span>
    <div style="float: right">
    	<a data-toggle="modal" href="#project-modal-new" class="btn btn-primary">New project</a>
    </div>
    </legend>
	<@project_modal_new.main/>
    <@project_list.main/>
</@layout.main>
