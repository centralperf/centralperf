<#import "/spring.ftl" as spring />
<#macro main>
	<div class="modal fade" id="project-modal-new" tabindex="-1" role="dialog" aria-labelledby="project-modal-new" aria-hidden="true">
		<div class="modal-dialog">
			<form method="post" action="${rc.contextPath}/project/new">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">Add a new project</h4>
				</div>
				<div class="modal-body">
						<fieldset>
							<label for="name">Name</label>
							<@spring.formInput "newProject.name", "class=input style=height:30px"/>
							<@spring.showErrors "<br/>", "cssError"/>
							<label></label><#-- bug in Twitter boostrap force to add empty label -->
						</fieldset>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					<button type="submit" class="btn btn-success">Create</button>
				</div>
			</div>
			</form>
		</div>
	</div>
</#macro>