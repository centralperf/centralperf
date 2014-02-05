<#macro main displayProject=true>
    <#if runs?size == 0>
        No runs
    <#else>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>Label</th>
                <th style="width: 18px"></th>
                <#if displayProject><th>Project</th></#if>
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
                <#if displayProject><td>${run.project.name}</td></#if>
                <td><#if run.launched><script>document.write(moment("${run.startDate?iso_utc}").fromNow())</script><#else><em>Not yet</em></#if>
                
                </td>
                <td>
                    <a href="${rc.contextPath}/project/${run.project.id}/script/${run.scriptVersion.script.id}/detail"
                    >${run.scriptVersion.script.label}
                    (${run.scriptVersion.number} - ${run.scriptVersion.description})
                    </a>
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
                     <a href="${rc.contextPath}/project/${run.project.id}/run/${run.id}/copy" title="Copy"><span class="glyphicon glyphicon-tags"></span></a>&nbsp;
                     <a href="${rc.contextPath}/project/${run.project.id}/run/${run.id}/detail" title="Detail"><span class="glyphicon glyphicon-search"></span></a>&nbsp;
                     <a href="${rc.contextPath}/project/${run.project.id}/run/${run.id}/delete" title="Delete"><span class="glyphicon glyphicon-trash"></span></a>&nbsp;
                </td>
            </tr>
        </#list>
        </table>
    </#if>
</#macro>