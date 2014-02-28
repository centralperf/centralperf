	<#if run.samples?size gt 0>
		<table class="table">
	    	<tr>
	        	<th>timestamp</th>
	            <th>elapsed</th>
	            <th>sampleName</th>
	            <th>returnCode</th>
	            <th>latency</th>
	            <th>sizeInOctet</th>
	            <th>assertResult</th>
	            <th>status</th>
			</tr>
			<#list run.samples as sample>
				<tr>
			    	<td>${sample.timestamp!}</td>
			        <td>${sample.elapsed!}</td>
			        <td title="${sample.sampleName!}"><#if sample.sampleName?? && sample.sampleName?length gt 15>${sample.sampleName?substring(0,15)}...<#else>${sample.sampleName!}</#if></td>
			        <td>${sample.returnCode!}</td>
			        <td>${sample.latency!}</td>
			        <td>${sample.sizeInOctet!}</td>
			        <td>${sample.assertResult!?string}</td>
			        <td>${sample.status!}</td>
				</tr>
			</#list>
		</table>
	</#if>