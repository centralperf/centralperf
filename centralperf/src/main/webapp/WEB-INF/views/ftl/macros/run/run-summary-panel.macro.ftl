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
<#macro main run>
		<style>
			.summaries > div{
				width		: 185px;
				height		: 95px;
				margin-right: 5px;
				margin-bottom: 5px;
				float		: left;		
				background-color : #E6E6FF;
			}
			.summaries > div > div:first-child {
				border-bottom:1px solid #CCCCCC;
				font-weight : bold;
				text-align	: center;
				background-color : #CFCFE6;
				padding-top		: 5px;
				padding-bottom	: 5px;
			}
			.summaries > div > div > div {
				font-size	: 13px;
			}	
			.summaries > div > div > div:nth-child(2) {
				font-size	: 15px;
				font-weight	: bold;
			}
		</style>
		<div class="summaries">
			<div>
				<div ><span class="glyphicon glyphicon-user"></span>&nbsp;Users</div>
				<div>
					<div class="col-xs-8">Current users</div>
					<div id="summaryCurrentUsers" class="col-xs-4">-</div>
				</div>
				<div>
					<div class="col-xs-8">Max users</div>
					<div id="summaryMaxUsers" class="col-xs-4">-</div>
				</div>
			</div>
			<div>
				<div><span class="glyphicon glyphicon-signal"></span>&nbsp;Bandwith</div>			
				<div>
					<div class="col-xs-5">Average</div>
					<div id="summaryCurrentBandwith" class="col-xs-7">-</div>
				</div>
				<div>
					<div class="col-xs-5">Total</div>
					<div id="summaryTotalBandwith" class="col-xs-7">-</div>
				</div>
			</div>
			<div>
				<div><span class="glyphicon glyphicon-stats"></span>&nbsp;Response times</div>					
				<div>
					<div class="col-xs-7">Avg. RT (ms)</div>
					<div id="summaryAverageResponseTime" class="col-xs-5">-</div>
				</div>
				<div>
					<div class="col-xs-7">Avg. lat. (ms)</div>
					<div id="summaryAverageLatency" class="col-xs-5">-</div>
				</div>			
			</div>
			<div>
				<div><span class="glyphicon glyphicon-random"></span>&nbsp;Requests</div>				
				<div>
					<div class="col-xs-8">Req. / s</div>
					<div id="summaryRequestPerSeconds" class="col-xs-4">-</div>
				</div>
				<div>
					<div class="col-xs-8">Total requests</div>
					<div id="summaryNumberOfSamples" class="col-xs-4">-</div>
				</div>			
			</div>
			<div>
				<div><span class="glyphicon glyphicon-wrench"></span>&nbsp;Errors</div>				
				<div>
					<div class="col-xs-8">Error rate</div>
					<div id="summaryErrorRate" class="col-xs-4">-</div>
				</div>			
			</div>
			<div>
				<div><span class="glyphicon glyphicon-dashboard"></span>&nbsp;Timing</div>	
				<div>
					<div class="col-xs-7">Started</div>
					<div id="summaryLaunchedDate" class="col-xs-5">-</div>
				</div>
				<div>
					<div class="col-xs-7">At</div>
					<div id="summaryLaunchedTime" class="col-xs-5">-</div>
				</div>
				<div>
					<div class="col-xs-7">Duration</div>
					<div id="summaryDuration" class="col-xs-5">-</div>
				</div>	
			</div>	
			<div class="clearfix" style="float:none"></div>
		</div>
</#macro>