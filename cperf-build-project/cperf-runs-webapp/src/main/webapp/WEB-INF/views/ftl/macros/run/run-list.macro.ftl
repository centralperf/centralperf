<#macro main>
    <#if runs?size == 0>
        No runs
    <#else>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>Label</th>
                <th style="width: 18px"></th>
                <th>Launched</th>
                <th>Script</th>
                <th class="column-with-btns"></th>
                <th class="column-with-btns"></th>
            </tr>
            </thead>

        <#list runs as run>
            <tr>
                <td><a href="${rc.contextPath}/project/${run.project.id}/run/${run.id}/detail" title="Detail">${run.label}</a></td>
                <td><#if run.running><img src="${rc.contextPath}/resources/img/lemming_running.gif" style="border: 0px"></#if></td>
                <td>${run.launched?string} <#if run.launched>(${run.startDate?string})</#if></td>
                <td>
                    <a href="${rc.contextPath}/project/${run.project.id}/script/${run.scriptVersion.id}/detail">${run.scriptVersion.number}</a>
                </td>
                <td>
                    <#if !run.running>
                        <a href="${rc.contextPath}/project/${run.project.id}/run/${run.id}/launch" class="btn-small btn-success" title="<#if run.launched>Relaunch<#else>Launch</#if>">
                            <#if run.launched>
                                <li class="icon-forward icon-white"></li>
                            <#else>
                                <li class="icon-play icon-white"></li>
                            </#if>
                        </a>
                    </#if>
                </td>
                <td class="column-with-btns">
                     <a href="${rc.contextPath}/project/${run.project.id}/run/${run.id}/copy" title="Copy"><i  class="icon-tags"></i></a>
                     <a href="${rc.contextPath}/project/${run.project.id}/run/${run.id}/detail" title="Detail"><i  class="icon-search" title="Detail"></i></a>
                     <a href="${rc.contextPath}/project/${run.project.id}/run/${run.id}/delete" title="Delete"><i  class="icon-trash" title="Delete"></i></a>
                </td>
            </tr>
        </#list>
        </table>
    </#if>
</#macro>