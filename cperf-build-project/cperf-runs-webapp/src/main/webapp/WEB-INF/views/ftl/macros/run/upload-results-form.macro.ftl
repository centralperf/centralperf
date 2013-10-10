<#import "/spring.ftl" as spring />
<#macro main run>
    <#if !run.launched>
    <legend>Upload results</legend>
    <form method="post" action="${rc.contextPath}/project/${run.project.id}/run/${run.id}/results" enctype="multipart/form-data">
        <fieldset>
            <label for="jmxFile">File in JMETER CSV format (JTL)</label>
            <input type="file" name="jtlFile" id="jtlFile" class="input"/>
            <label></label><#-- bug in Twitter boostrap force to add empty label -->
            <input type="submit" value="Create" class="btn btn-primary"/>
        </fieldset>
    </form>
    </#if>
</#macro>