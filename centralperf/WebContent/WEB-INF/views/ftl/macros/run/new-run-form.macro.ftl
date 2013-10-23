<#import "/spring.ftl" as spring />
<#import 'upload-results-form.macro.ftl' as upload_results_form>

    <legend>${importRun?has_content?string("Import","New")} run</legend>
    
    <#-- Project is known -->
    <#if project?exists>
        <form method="post" action="${rc.contextPath}/project/${project.id}/run/${importRun?has_content?string("import","new")}"
                ${importRun?has_content?string("enctype='multipart/form-data'","")}
                >
            <fieldset>
                <@spring.bind "newRun.project.id" />
                <input type="hidden" name="${spring.status.expression}" value="${project.id}"/>      
				<@spring.bind "newRun.scriptVersion.id" />
                <label for="${spring.status.expression}">Script</label></td>
                <select name="${spring.status.expression}" class="input">
                    <#list project.scripts as script>
                        <option value="${script.versions[script.versions?size -1].id}">${script.label} (version ${script.versions?size})</option>
                    </#list>
                </select>
                
    <#-- Select a project before-->
    <#else>
        <form id="newRunForm" method="post" action="">
        	<fieldset>        		
        		<@spring.bind "newRun.project.id" />
        		<label for="${spring.status.expression}">Project</label></td>
                <select name="${spring.status.expression}" class="input" onchange="onProjectChange(this)">
                	<option value="-1"></option>                
                    <#list projects as project>
                        <option value="${project.id}">${project.name}</option>
                    </#list>
                </select>
                <script>
                	function onProjectChange(target){
                		$('[id^=scriptForProject]').hide();
                		$('#scriptForProject' + target.value).show();
                		$('#newRunForm').attr('action','${rc.contextPath}/project/' + target.value + '/run/new');
                	}
                </script>
                <#-- Oui, c'est sale, j'assume -->
                <#list projects as project>
					<@spring.bind "newRun.scriptVersion.id" />
					<div id="scriptForProject${project.id}" style="display:none">
	                	<label for="${spring.status.expression}">Script</label></td>                
		                <select name="${spring.status.expression}" class="input">
		                    <#list project.scripts as script>
		                        <option value="${script.versions[script.versions?size -1].id}">${script.label} (version ${script.versions?size})</option>
		                    </#list>
		                </select>
	                </div>
                </#list>                
	</#if>
                <@spring.bind "newRun.label" />
                <label for="${spring.status.expression}">Label</label>
                <@spring.formInput "newRun.label", "class=input style=height:30px"/>
                <@spring.showErrors "<br/>", "cssError"/>


                <#if importRun?has_content>
                    <@upload_results_form.main/>
                </#if>

                <label></label><#-- bug in Twitter boostrap force to add empty label -->
                <input type="submit" value="Create" class="btn btn-success"/>
            </fieldset>
        </form>