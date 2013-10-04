<#import "/spring.ftl" as spring />
<#macro main project>
    <legend>New Run</legend>
    <#if project.scripts?size = 0>
        No script available... Please create one
    <#else>
        <form method="post" action="${rc.contextPath}/run/new" class="form-inline">
            <legend>New run</legend>
            <@spring.bind "newRun.project.id" />
            <input type="hidden" name="${spring.status.expression}" value="${project.id}"/>
            <@spring.bind "newRun.label" />
            <label for="${spring.status.expression}">Label</label>
            <@spring.formInput "newRun.label", "class=input style=height:30px"/>
            <@spring.showErrors "<br/>", "cssError"/>
            <@spring.bind "newRun.script.id" />
            <label for="${spring.status.expression}">Script</label></td>
            <select name="${spring.status.expression}" class="input">
                <#list scripts as script>
                    <option value="${script.id}">${script.label}</option>
                </#list>
            </select>
            <input type="submit" value="Create" class="btn btn-primary"/>
        </form>
    </#if>
</#macro>