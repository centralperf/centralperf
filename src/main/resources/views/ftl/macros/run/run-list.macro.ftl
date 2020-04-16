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
<#import '../utils.macro.ftl' as utils>
<#macro main displayProject=true>
    <#if runs?size == 0>
        No runs
    <#else>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>Label</th>
                <th style="width: 18px"></th>
                <#if displayProject>
                    <th>Project</th>
                </#if>
                <th>Schedule</th>
                <th>Launched</th>
                <th>Script</th>
                <th class="column-with-btns"></th>
                <th class="column-with-btns"></th>
            </tr>
            </thead>

            <#list runs as run>
                <tr>
                    <td><a href="${rc.contextPath}/project/${run.project.id?c}/run/${run.id?c}/detail"
                           title="Detail">${run.label}</a></td>
                    <td><#if run.running><img src="${rc.contextPath}/resources/img/lemming_running.gif"
                                              style="border: 0px"></#if></td>
                    <#if displayProject>
                        <td><a href="${rc.contextPath}/project/${run.project.id?c}/detail"
                               title="Project">${run.project.name}</a>
                        </td>
                    </#if>
                    <td>
                        <#if !run.finished && (run.scheduledDate?? || run.scheduleCronExpression??)>
                            <#if run.scheduledDate??><span class="glyphicon glyphicon-time"
                                                           aria-hidden="true"></span><@utils.prettyDate run.scheduledDate/></#if>
                            <#if run.scheduleCronExpression??><span class="glyphicon glyphicon-refresh"
                                                                    aria-hidden="true"></span>&nbsp;${run.scheduleCronExpression}</#if>
                        <#else>
                            -
                        </#if>
                    </td>
                    <td><#if run.launched && run.lastStartDate??><@utils.prettyDate run.lastStartDate/><#else><em>Not
                            yet</em></#if></td>
                    <td>
                        <a href="${rc.contextPath}/project/${run.project.id?c}/script/${run.scriptVersion.script.id?c}/detail"
                        >${run.scriptVersion.script.label}
                            (${run.scriptVersion.number} - <@utils.truncate run.scriptVersion.description/>)
                        </a>
                    </td>
                    <td>
                        <#if run.finished>
                            <a href="${rc.contextPath}/project/${run.project.id?c}/run/${run.id?c}/launch"
                               class="btn btn-success btn-sm" title="Relaunch">
                                <span class="glyphicon glyphicon-forward"></span>
                            </a>
                        <#else>
                            <#if !run.launched>
                                <a href="${rc.contextPath}/project/${run.project.id?c}/run/${run.id?c}/launch"
                                   class="btn btn-success btn-sm" title="Launch">
                                    <span class="glyphicon glyphicon-play"></span>
                                </a>
                            </#if>
                        </#if>
                    </td>
                    <td class="column-with-btns">
                        <a href="${rc.contextPath}/project/${run.project.id?c}/run/${run.id?c}/copy" title="Copy"><span
                                    class="glyphicon glyphicon-tags"></span></a>&nbsp;
                        <a href="${rc.contextPath}/project/${run.project.id?c}/run/${run.id?c}/detail"
                           title="Detail"><span class="glyphicon glyphicon-search"></span></a>&nbsp;
                        <a href="${rc.contextPath}/project/${run.project.id?c}/run/${run.id?c}/delete"
                           title="Delete"><span class="glyphicon glyphicon-trash"></span></a>&nbsp;
                    </td>
                </tr>
            </#list>
        </table>
    </#if>
</#macro>