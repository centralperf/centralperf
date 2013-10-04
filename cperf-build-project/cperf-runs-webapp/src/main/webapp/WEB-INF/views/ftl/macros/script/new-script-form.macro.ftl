<#import "/spring.ftl" as spring />
<#macro main project>
    <legend>New script</legend>
    <form method="post" action="${rc.contextPath}/script/new" enctype="multipart/form-data" class="form-inline">
        <@spring.bind "newScript.project.id" />
        <input type="hidden" name="${spring.status.expression}" value="${project.id}"/>
        <label for="label">Label</label>
        <@spring.formInput "newScript.label", "class=input style=height:30px"/>
        <@spring.showErrors "<br/>", "cssError"/>
        <label for="jmxFile">JMX File</label>
        <input type="file" name="jmxFile" id="jmxFile" class="input"/>
        <input type="submit" value="New script" class="btn"/>
    </form>
</#macro>