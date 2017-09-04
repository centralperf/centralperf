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
<#macro main>
    <#if projects?size == 0>
        No projects yet...
    <#else>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Scripts</th>
                <th>Runs</th>
                <th>Running ?</th>
                <th class="column-with-btns"></th>
            </tr>
            </thead>

            <#list projects as project>
                <tr>
                    <td><a href="${rc.contextPath}/project/${project.id}/detail" title="Detail">${project.name}</a></td>
                    <td>${project.description!}</td>
                    <td>${project.scripts?size}</td>
                    <td>${project.runs?size}</td>
                    <td>
                        <#list project.runs as run>
                            <#if run.running>
                                <img src="${rc.contextPath}/resources/img/lemming_running.gif" style="border: 0px">
                            </#if>
                        </#list>
                    </td>
                    <td class="column-with-btns">
                        <a href="${rc.contextPath}/project/${project.id}/detail" title="Detail"><span class="glyphicon glyphicon-search"></span></a>&nbsp;
                        <a href="${rc.contextPath}/project/${project.id}/delete" title="Delete"><span class="glyphicon glyphicon-trash"></span></a>&nbsp;
                    </td>
                </tr>
            </#list>
        </table>
    </#if>
</#macro>
