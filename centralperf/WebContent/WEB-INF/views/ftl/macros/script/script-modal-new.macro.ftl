<#import "/spring.ftl" as spring />
<#macro main>
	<div class="modal fade" id="script-modal-new" tabindex="-1" role="dialog" aria-labelledby="run-modal-new" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">Add a new script</h4>
				</div>
				<div class="modal-body">
                	<form method="post" action="${rc.contextPath}/project/${project.id}/script/new" enctype="multipart/form-data" class="form-horizontal">
				            <@spring.bind "newScript.project.id" />
				            <input type="hidden" name="${spring.status.expression}" value="${project.id}"/>
				
							<#-- INPUT SCRIPT NAME -->
							<div class="form-group">
				            	<label for="label">Label</label>
			            		<@spring.formInput "newScript.label", "class=form-control"/>
				            	<@spring.showErrors "<br/>", "cssError"/>
							</div>
							
							<#-- INPUT SCRIPT DESC -->
							<div class="form-group">
				           		<label for="label">Description</label>
			            		<@spring.formTextarea "newScript.description", "class=form-control rows=3"/>
				            	<@spring.showErrors "<br/>", "cssError"/>
							</div>
				
							<#-- INPUT SCRIPT JMX FILE -->
							<div class="form-group">
				            	<label for="jmxFile">JMX File</label>
			            		<input type="file" name="jmxFile" id="jmxFile" class="form-control"/>
							</div>
				            
							<#-- CREATE BUTTON -->
							<div class="form-group">
								<label class="control-label"></label>
   								<input type="submit" value="Create" class="btn btn-success"/>
							</div>
					</form>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
				</div>
			</div>
		</div>
	</div>
</#macro>
