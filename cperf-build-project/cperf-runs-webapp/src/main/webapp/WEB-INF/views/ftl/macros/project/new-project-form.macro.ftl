<#import "/spring.ftl" as spring />
<#macro main>
    <form method="post" action="${rc.contextPath}/project/new">
        <fieldset>
            <label for="name">Name</label>
            <@spring.formInput "newProject.name", "class=input style=height:30px"/>
            <@spring.showErrors "<br/>", "cssError"/>
            <label></label><#-- bug in Twitter boostrap force to add empty label -->
            <button type="submit" class="btn btn-success">Create</button>
        </fieldset>
    </form>
</#macro>