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
					<div class="col-xs-7">Bandwith</div>
					<div id="summaryCurrentBandwith" class="col-xs-5">-</div>
				</div>
				<div>
					<div class="col-xs-7">Total bandwith</div>
					<div id="summaryTotalBandwith" class="col-xs-5">-</div>
				</div>
			</div>
			<div>
				<div><span class="glyphicon glyphicon-stats"></span>&nbsp;Response times</div>					
				<div>
					<div class="col-xs-7">Avg. resp. time</div>
					<div id="summaryAverageResponseTime" class="col-xs-5">-</div>
				</div>
				<div>
					<div class="col-xs-7">Avg. latency</div>
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
					<div class="col-xs-8">Launched time</div>
					<div id="summaryLaunchedTime" class="col-xs-4">-</div>
				</div>
				<div>
					<div class="col-xs-8">Duration</div>
					<div id="summaryDuration" class="col-xs-4">-</div>
				</div>	
			</div>	
			<div class="clearfix" style="float:none"></div>
		</div>
</#macro>