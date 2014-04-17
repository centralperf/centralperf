<#--
  Copyright (C) 2014  The Central Perf authors
 
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as
  published by the Free Software Foundation, either version 3 of the
  License, or (at your option) any later version.
 
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.
 
  You should have received a copy of the GNU Affero General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->
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
							<@spring.formInput "newProject.name", "class=form-control style=height:30px"/>
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