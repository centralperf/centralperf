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
                    <td>Description</td>
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