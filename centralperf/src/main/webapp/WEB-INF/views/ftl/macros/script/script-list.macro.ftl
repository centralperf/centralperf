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
<#macro main scripts>
    <#if scripts?size == 0>
     No scripts
    <#else>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>Label</th>
                <th>Description</th>
                <th>Type</th>
                <th>Versions</th>
                <th nowrap>Version info</th>
                <th class="column-with-btns"></th>
            </tr>
            </thead>

            <#list scripts as script>
                <#assign project=script.project/>
                <tr>
                    <td><a href="${rc.contextPath}/project/${project.id}/script/${script.id}/detail">${script.label}</a></td>
                    <td>${script.description!}</td>
                    <td>${script.samplerUID!}</td>
                    <td>${script.versions?size!}</td>
                    <td>${script.versions[0].description}</td>
                    <td class="column-with-btns" style="width:100px">
                        <a href="${rc.contextPath}/project/${project.id}/script/${script.id}/detail"><span class="glyphicon glyphicon-search"></span></a>&nbsp;
                        <a href="${rc.contextPath}/project/${project.id}/script/${script.id}/delete"><span class="glyphicon glyphicon-trash"/></span></a>&nbsp;
                    </td>
                </tr>
            </#list>
        </table>
    </#if>
</#macro>