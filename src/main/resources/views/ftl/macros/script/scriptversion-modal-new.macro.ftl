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
	<div class="modal fade" id="scriptVersion-modal-new" tabindex="-1" role="dialog"
		 aria-labelledby="scriptVersion-modal-new" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<form method="post" action="${rc.contextPath}/project/${project.id?c}/script/${script.id?c}/version/new"
					  enctype="multipart/form-data">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title">Add a new version</h4>
					</div>
					<div class="modal-body">
						<@spring.bind "newScriptVersion.script.id" />
						<input type="hidden" name="${spring.status.expression}" value="${script.id?c}"/>

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
