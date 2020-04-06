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
	<form id="scheduleForm" method="post"
		  action="${rc.contextPath}/project/${run.project.id?c}/run/${run.id?c}/schedule">
		<div class="modal fade" id="run-modal-schedule" tabindex="-1" role="dialog" aria-labelledby="run-modal-schedule"
			 aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
						<h4 class="modal-title">Schedule run</h4>
					</div>
					<div class="modal-body">
						<#if !run.launched || run.scheduledDate??>
						<div class="panel-group" id="scheduleAccordion" role="tablist" aria-multiselectable="true">
							<div class="panel panel-default">
								<div class="panel-heading" role="tab" id="headingOne">
									<h4 class="panel-title">
										<span class="glyphicon glyphicon-time" aria-hidden="true"></span>
										<a id="delayedRunHeader" role="button" data-toggle="collapse"
										   data-parent="#scheduleAccordion" href="#collapseScheduleDate"
										   aria-expanded="true" aria-controls="collapseScheduleDate">
											Delayed run
										</a>
									</h4>
								</div>
								<div id="collapseScheduleDate" class="panel-collapse collapse in" role="tabpanel"
									 aria-labelledby="headingOne">
									<div class="panel-body">
										<div class="form-group">
											<div class="row">
												<div class="col-md-12">
													<div id="scheduledDateTimePicker"></div>
												</div>
											</div>
										</div>
										<@spring.bind "run.scheduledDate" />
										<@spring.formHiddenInput "run.scheduledDate"/>
										<@spring.showErrors "<br/>", "cssError"/>
									</div>
								</div>
							</div>
							</#if>
							<#if !run.launched || run.scheduleCronExpression??>
								<div class="panel panel-default">
									<div class="panel-heading" role="tab" id="headingTwo">
										<h4 class="panel-title">
											<span class="glyphicon glyphicon-refresh" aria-hidden="true"></span>
											<a id="cronRunHeader" class="collapsed" role="button" data-toggle="collapse"
											   data-parent="#scheduleAccordion" href="#collapseCron"
											   aria-expanded="false" aria-controls="collapseCron">
												Cron run
											</a>
										</h4>
									</div>
									<div id="collapseCron" class="panel-collapse collapse" role="tabpanel"
										 aria-labelledby="headingTwo">
										<div class="panel-body">
											<div class="form-group">
												<@spring.bind "run.scheduleCronExpression" />
												<label for="${spring.status.expression}">Cron run</label>
												<div id="scheduleCronExpressionInputGroup" class="input-group">
													<@spring.formInput "run.scheduleCronExpression", "aria-describedby='helpCron' class=form-control style=height:30px maxLength=33"/>
													<span id="cronInfoBtn" type="button"
														  class="input-group-addon btn btn-primary glyphicon glyphicon-ok cron-info-btn"></span>
													<div id="cronHumanReadable"
														 class="input-group-addon cron-info-addon">
														<#if run.scheduleCronExpression??>${cronExpressionReadable}. Next start <@utils.prettyDate nextStartDate/></#if>
													</div>
												</div>
												<span id="helpCron" class="help-block">Cron syntax (6 fields) : second, minute, hour, day of month, month, day(s) of week. Example : 0 0/10 * * * * = every ten minutes</span>
											</div>
										</div>
									</div>
								</div>
							</#if>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
						<button type="submit" id="scheduleSubmitBtn" class="btn btn-success">
							<#if !run.launched>
								Schedule
							<#elseif run.scheduledDate?? || run.scheduleCronExpression??>
								Modify schedule
							</#if>
						</button>
					</div>
				</div>
			</div>
		</div>
	</form>
	<script>
		let activePanel = "delayed";
		let scheduledDate = "<#if run.scheduledDate??>${run.scheduledDate?datetime?iso_utc}</#if>";
		$(function () {

			// Global
			$("#delayedRunHeader").click(function () {
				activePanel = "delayed";
			});
			$("#cronRunHeader").click(function () {
				activePanel = "cron";
			});
			$("#scheduleForm").submit(function () {
				if ("delayed" === activePanel) {
					$("#scheduleCronExpression").val("");
				} else {
					$("#scheduledDate").val("");
				}
			});
			$("#scheduleSubmitBtn").prop('disabled', true);

			// Scheduled date
			$("#scheduledDateTimePicker")
					.datetimepicker({
						<#if run.scheduledDate??>
						defaultDate: scheduledDate,
						</#if>
						minDate: new Date(),
						inline: true,
						sideBySide: true,
						useCurrent: false
					})
					.on("dp.change", function (e) {
						$("#scheduleSubmitBtn").prop('disabled', false);
						$("#scheduledDate").val(e.date.toISOString());
					});

			// Cron expression
			function validateCronExpression(cronExpression) {
				$.getJSON("${rc.contextPath}/api/cron/format?expression=" + cronExpression,
						function (response) {
							if (response.valid) {
								$("#cronHumanReadable").text(response.humanReadable + ". Next " + moment(response.next).fromNow());
								$("#scheduleSubmitBtn").prop("disabled", false);
								$("#scheduleCronExpressionInputGroup").removeClass("has-error");
								$("#scheduleCronExpressionInputGroup").addClass("has-success");
							} else {
								$("#cronHumanReadable").text("Syntax error : " + response.validationErrorMesssage);
								$("#scheduleSubmitBtn").prop("disabled", true);
								$("#scheduleCronExpressionInputGroup").removeClass("has-success");
								$("#scheduleCronExpressionInputGroup").addClass("has-error");
							}
						}
				);
			}

			$("#cronInfoBtn").click(function () {
				validateCronExpression($("#scheduleCronExpression").val());
			});
			$("#scheduleCronExpression").change(function () {
				validateCronExpression($(this).val());
			});

			<#if run.scheduleCronExpression??>
			$("#collapseCron").collapse('show');
			activePanel = "cron";
			</#if>
		});
	</script>
</#macro>


