<#macro main>
    <#if scripts?size == 0>
     No scripts
    <#else>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>Label</th>
                <th>Description</th>
                <th class="column-with-btns"></th>
            </tr>
            </thead>

            <#list scripts as script>
                <tr>
                    <td><a href="${rc.contextPath}/script/${script.id}/detail">${script.label}</a></td>
                    <td>${script.description!}</td>
                    <td class="column-with-btns" style="width:200px">
                        <a href="${rc.contextPath}/script/${script.id}/detail"><i  class="icon-search" title="Detail"></i></a>
                        <a href="${rc.contextPath}/script/${script.id}/delete"><i  class="icon-trash" title="Delete"></i></a>
                    </td>
                </tr>
            </#list>
        </table>
    </#if>
</#macro>