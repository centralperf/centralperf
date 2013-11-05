<#import "/spring.ftl" as spring />
<#macro main action="new">
	<form method="post" action="${rc.contextPath}/project/${project.id}/run/${action}" class="form-horizontal" ${(action == "import")?string('enctype="multipart/form-data"','')}>
		<div class="modal fade" id="run-modal-${action}" tabindex="-1" role="dialog" aria-labelledby="run-modal-new" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title">${(action == "new")?string('Add','Import')} a new run</h4>
					</div>
					<div class="modal-body">
						<@spring.bind "newRun.project.id" />
						<input type="hidden" name="${spring.status.expression}" value="${project.id}"/>                						

						<div class="form-group">
							<@spring.bind "newRun.label" />
							<label for="${spring.status.expression}">Label</label>
							<@spring.formInput "newRun.label", "class=form-control style=height:30px"/>
							<@spring.showErrors "<br/>", "cssError"/>
						</div>
									
						<div class="form-group">
							<@spring.bind "newRun.scriptVersion.id" />
							<label for="${spring.status.expression}">Script</label></td>
							<select name="${spring.status.expression}" class="form-control">
								<#list project.scripts as script>
									<option value="${script.versions[script.versions?size -1].id}">${script.label} (version ${script.versions?size})</option>
								</#list>
							</select>
						</div>
						
						<#if action == "import">
							<div class="form-group">
					       		<label for="jmxFile">File in JMETER CSV format (JTL)</label>
				            	<input type="file" name="jtlFile" id="jtlFile" class="form-control"/>
				            </div>
						</#if>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
						<input type="submit" value="Create" class="btn btn-success"/>					
					</div>
				</div>
			</div>
		</div>
	</form>
</#macro>


