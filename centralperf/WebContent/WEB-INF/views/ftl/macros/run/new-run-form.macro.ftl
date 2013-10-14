<#import "/spring.ftl" as spring />
<#import 'upload-results-form.macro.ftl' as upload_results_form>

    <#if project.scripts?size = 0>
        No script available... Please create one before
    <#else>
    <legend>${importRun?has_content?string("Import","New")} run</legend>
        <form method="post" action="${rc.contextPath}/project/${project.id}/run/${importRun?has_content?string("import","new")}"
                ${importRun?has_content?string("enctype='multipart/form-data'","")}
                >
            <fieldset>
                <@spring.bind "newRun.project.id" />
                <input type="hidden" name="${spring.status.expression}" value="${project.id}"/>
                <@spring.bind "newRun.label" />
                <label for="${spring.status.expression}">Label</label>
                <@spring.formInput "newRun.label", "class=input style=height:30px"/>
                <@spring.showErrors "<br/>", "cssError"/>
                <@spring.bind "newRun.scriptVersion.id" />
                <label for="${spring.status.expression}">Script</label></td>
                <select name="${spring.status.expression}" class="input">
                    <#list project.scripts as script>
                        <option value="${script.versions[script.versions?size -1].id}">${script.label} (version ${script.versions?size})</option>
                    </#list>
                </select>

                <#if importRun?has_content>
                    <@upload_results_form.main/>
                </#if>

                <label></label><#-- bug in Twitter boostrap force to add empty label -->
                <input type="submit" value="Create" class="btn btn-success"/>
            </fieldset>
        </form>
    </#if>