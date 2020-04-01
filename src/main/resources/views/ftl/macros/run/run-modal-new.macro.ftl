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
<#macro main action="new">
	<form method="post"
		  action="${rc.contextPath}/project/${project.id?c}/run/${action}" ${(action == "import")?string('enctype="multipart/form-data"','')}>
		<div class="modal fade" id="run-modal-${action}" tabindex="-1" role="dialog" aria-labelledby="run-modal-new"
			 aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title">${(action == "new")?string('Add','Import')} a new run</h4>
					</div>
					<div class="modal-body">
						<@spring.bind "newRun.project.id" />
						<input type="hidden" name="${spring.status.expression}" value="${project.id?c}"/>

						<div class="form-group">
							<@spring.bind "newRun.label" />
							<label for="${spring.status.expression}">Label</label>
							<@spring.formInput "newRun.label", "class=form-control style=height:30px maxLength=33"/>
							<@spring.showErrors "<br/>", "cssError"/>
						</div>

						<div class="form-group">
							<label for="script">Script</label></td>
							<select id="${action}_scriptSelect" class="form-control">
								<option value="">Select a script</option>
								<option value="">---------------</option>
								<#list project.scripts as script>
									<option value="${script.id?c}">${script.label} - ${script.samplerUID}</option>
								</#list>
							</select>
							<script>
								$("#${action}_scriptSelect").unbind();
								var ${action}_previousId = -1;
								$("#${action}_scriptSelect").change(function(){
									var scriptId = this.value;
									if(scriptId != ""){
										$("#${action}_versions").show();
										$("#${action}_version" + ${action}_previousId).hide();
										$("#${action}_version" + scriptId).show();
										$("#${action}_version" + scriptId).select();
										${action}_previousId = scriptId;
									}
								});
							</script>
						</div>
						
						<div id="${action}_versions" class="form-group" style="display:none">
							<@spring.bind "newRun.scriptVersion.id" />
							<input type="hidden" id="${spring.status.expression}" name="${spring.status.expression}" value="${project.id?c}"/>  
							<label>Script Version</label></td>
							<#list project.scripts as script>						
								<select id="${action}_version${script.id?c}" class="form-control"  style="display:none">
									<#list script.versions?sort_by("number")?reverse as version>
										<option value="${version.id?c}">${version.number} - ${version.description}</option>
									</#list>		
								</select>
								<script>
									$("#${action}_version${script.id?c}").unbind();
									$("#${action}_version${script.id?c}").bind("change select", function(){
											$("#${spring.status.expression?replace('.','\\\\.')}").val(this.value);
									});
								</script>								
							</#list>
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


