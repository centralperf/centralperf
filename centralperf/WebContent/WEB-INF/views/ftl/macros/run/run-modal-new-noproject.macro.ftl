<#import "/spring.ftl" as spring />
<#macro main>
	<div class="modal fade" id="run-modal-new" tabindex="-1" role="dialog" aria-labelledby="run-modal-new" aria-hidden="true">
		<div class="modal-dialog">
			<form method="post" action="#" name="run-modal-new_form" id="run-modal-new_form" class="form-horizontal">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title">Add a new run</h4>
				</div>
				<div class="modal-body">
						<fieldset>
							<#-- SELECT PROJECT -->
							<div class="control-group">
								<@spring.bind "newRun.project.id" />
								<label class="control-label" for="${spring.status.expression}">Project</label>
								<div class="controls">
									<select name="${spring.status.expression}" class="selectpicker PList" id="projectList" title="Not loaded ...">
			                		</select>
								</div>
							</div>
							
							<#-- INPUT NAME -->
							<div class="control-group">
								<@spring.bind "newRun.label" />
								<label class="control-label" for="${spring.status.expression}">Label</label>
								<div class="controls">
									<@spring.formInput "newRun.label", "class=input-xlarge style=height:30px"/>
								</div>
								<@spring.showErrors "<br/>", "cssError"/>
							</div>
							
							<#-- SELECT SCRIPT -->
							<div class="control-group">
								<@spring.bind "newRun.scriptVersion.id" />
								<label class="control-label" for="${spring.status.expression}">Script</label>
								<div class="controls">
									<select name="${spring.status.expression}"  class="selectpicker SList" id="scriptList" title="Select project first">
			                		</select>
								</div>
							</div>
							
						</fieldset>
					<script type="text/javascript" charset="utf-8">
						$('#run-modal-new').on('shown.bs.modal', function () {
							$.getJSON("${rc.contextPath}/project/json/list",'', function(p){
								var options = '<option value="NOSELECT">Select a project</option>';
						    	for (var i = 0; i < p.length; i++) {
						    		options += '<option value="' + p[i].id + '">' + p[i].name + '</option>';
						    	}
						    	$("select#projectList").html(options);
						    	$('.PList').selectpicker('refresh');
		   					});
		   				})
		   				$('#projectList').on('change', function (e) {
		   					var sel = $("select#projectList").val();
		   					if(sel!="NOSELECT"){
		   						$("#run-modal-new_form").attr("action", "${rc.contextPath}/project/"+sel+"/run/new");
								$.getJSON("${rc.contextPath}/project/"+sel+"/script/json/list",'', function(p){
									var options = '';
							    	for (var i = 0; i < p.length; i++) {
							    		options += '<option value="' + p[i].id + '">' + p[i].label + '</option>';
							    	}
							    	$("select#scriptList").html(options);
							    	$('.SList').selectpicker('refresh');
			   					});
			   				}
		   				})
		   				
					</script>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					<input type="submit" value="Create" class="btn btn-success"/>
				</div>
			</div>
			</form>
		</div>
	</div>
</#macro>


