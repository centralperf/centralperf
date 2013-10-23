<#import "/spring.ftl" as spring />
<#macro main>
	<div class="modal fade" id="run-modal-new" tabindex="-1" role="dialog" aria-labelledby="run-modal-new" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">Add a new run</h4>
				</div>
				<div class="modal-body">
					<legend>New run</legend>
                	<form method="post" action="${rc.contextPath}/project/${project.id}/run/new">
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
										
							<label></label><#-- bug in Twitter boostrap force to add empty label -->
							<input type="submit" value="Create" class="btn btn-primary"/>
						</fieldset>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
</#macro>


