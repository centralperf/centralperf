<#macro main>
    <#if projects?size == 0>
        No projects yet... <a href="${rc.contextPath}/project">Create one !</a>
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
                        <a href="${rc.contextPath}/project/${project.id}/detail" title="Detail"><i  class="icon-search" title="Detail"></i></a>
                        <a href="${rc.contextPath}/project/${project.id}/delete" title="Delete"><i  class="icon-trash" title="Delete"></i></a>
                    </td>
                </tr>
            </#list>
        </table>
    </#if>
</#macro>