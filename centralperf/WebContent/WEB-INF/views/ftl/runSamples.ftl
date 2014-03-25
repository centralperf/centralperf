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