<#import "/spring.ftl" as spring />
    <legend>Create a new script</legend>
    <form method="post" action="${rc.contextPath}/project/${project.id}/script/new" enctype="multipart/form-data">
        <fieldset>
            <@spring.bind "newScript.project.id" />
            <input type="hidden" name="${spring.status.expression}" value="${project.id}"/>

            <label for="label">Label</label>
            <@spring.formInput "newScript.label", "class=input style=height:30px"/>
            <@spring.showErrors "<br/>", "cssError"/>

            <label for="label">Description</label>
            <@spring.formTextarea "newScript.description", "class=input rows=3"/>
            <@spring.showErrors "<br/>", "cssError"/>

            <label for="jmxFile">JMX File</label>
            <input type="file" name="jmxFile" id="jmxFile" class="input"/>
            <label></label><#-- bug in Twitter boostrap force to add empty label -->
            <input type="submit" value="Create script" class="btn btn-success"/>
        </fieldset>
    </form>
