<#--
  Copyright (C) 2020  The Central Perf authors
 
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
<#macro main run>
	<form method="post" action="${rc.contextPath}/project/${run.project.id?c}/run/${run.id?c}/schedule">
		<div class="modal fade" id="run-modal-schedule" tabindex="-1" role="dialog" aria-labelledby="run-modal-schedule"
			 aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title">Schedule run</h4>
					</div>
					<div class="modal-body">
						<div class="form-group">
							<@spring.bind "run.scheduledDate" />
							<label for="${spring.status.expression}">Delayed run</label>
							<@spring.formInput "run.scheduledDate", "class=form-control style=height:30px maxLength=33"/>
							<@spring.showErrors "<br/>", "cssError"/>
						</div>
						<div class="form-group">
							<@spring.bind "run.scheduleCronExpression" />
							<label for="${spring.status.expression}">Cron run</label>
							<@spring.formInput "run.scheduleCronExpression", "class=form-control style=height:30px maxLength=33"/>
							<@spring.showErrors "<br/>", "cssError"/>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
						<input type="submit" value="Schedule" class="btn btn-success"/>
					</div>
				</div>
			</div>
		</div>
	</form>
</#macro>


