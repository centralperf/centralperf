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
						<fieldset>
							<legend>Create a new script</legend>
							
				            <@spring.bind "newScript.project.id" />
				            <input type="hidden" name="${spring.status.expression}" value="${project.id}"/>
				
							<#-- INPUT SCRIPT NAME -->
							<div class="control-group">
				            	<label for="label">Label</label>
				            	<div class="controls">
				            		<@spring.formInput "newScript.label", "class=input style=height:30px"/>
				            	</div>
				            	<@spring.showErrors "<br/>", "cssError"/>
							</div>
							
							<#-- INPUT SCRIPT DESC -->
							<div class="control-group">
				           		<label for="label">Description</label>
				           		<div class="controls">
				            		<@spring.formTextarea "newScript.description", "class=input rows=3"/>
				            	</div>
				            	<@spring.showErrors "<br/>", "cssError"/>
							</div>
				
							<#-- INPUT SCRIPT JMX FILE -->
							<div class="control-group">
				            	<label for="jmxFile">JMX File</label>
				            	<div class="controls">
				            		<input type="file" name="jmxFile" id="jmxFile" class="input"/>
				            	</div>
							</div>
				            
							<#-- CREATE BUTTON -->
							<div class="control-group">
								<label class="control-label"></label>
  								<div class="controls">
   									<input type="submit" value="Create" class="btn btn-success"/>
  								</div>
							</div>
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
