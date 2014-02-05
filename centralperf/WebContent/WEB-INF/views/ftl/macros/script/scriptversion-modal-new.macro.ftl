<#import "/spring.ftl" as spring />
<#macro main>
	<div class="modal fade" id="scriptVersion-modal-new" tabindex="-1" role="dialog" aria-labelledby="scriptVersion-modal-new" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
                <form method="post" action="${rc.contextPath}/project/${project.id}/script/${script.id}/version/new" enctype="multipart/form-data" class="form-horizontal">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">Add a new version</h4>
				</div>
				<div class="modal-body">
				            <@spring.bind "newScriptVersion.script.id" />
				            <input type="hidden" name="${spring.status.expression}" value="${script.id}"/>

							<#-- INPUT SCRIPT DESC -->
							<div class="form-group">
				           		<label for="label">Description</label>
			            		<@spring.formTextarea "newScriptVersion.description", "class=form-control rows=3"/>
				            	<@spring.showErrors "<br/>", "cssError"/>
							</div>
				
							<#-- INPUT SCRIPT SOURCE FILE -->
							<div class="form-group">
				            	<label for="scriptFile">File</label>
			            		<input type="file" name="scriptFile" id="scriptFile" class="form-control"/>
							</div>
				            
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
   					<input type="submit" value="Create" class="btn btn-success"/>
				</div>
			</form>
			</div>
		</div>
	</div>
</#macro>
