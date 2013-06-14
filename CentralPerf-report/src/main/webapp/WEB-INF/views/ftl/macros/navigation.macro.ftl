<#macro main>
<div id="navigation-menu">
	<ul>
		<#if !menu?has_content || menu="report_sum"><li id="selected"><span>Summary</span></li><#else><li><a href="${rc.contextPath}/report/${run.id}/sum"><span>Summary</span></a></li></#if>
		<#if !menu?has_content || menu="report_grt"><li id="selected"><span>Global RT</span></li><#else><li><a href="${rc.contextPath}/report/${run.id}/grt"><span>Global RT</span></a></li></#if>
		<#if !menu?has_content || menu="report_prt"><li id="selected"><span>Pages RT</span></li><#else><li><a href="${rc.contextPath}/report/${run.id}/prt"><span>Pages RT</span></a></li></#if>
		<#if !menu?has_content || menu="report_ert"><li id="selected"><span>RT Evolution</span></li><#else><li><a href="${rc.contextPath}/report/${run.id}/ert"><span>RT Evolution</span></a></li></#if>
		<#if !menu?has_content || menu="report_exp"><li id="selected"><span>Export Results</span></li><#else><li><a href="${rc.contextPath}/report/${run.id}/exp"><span>Export Results</span></a></li></#if>
	</ul>
</div>
<br/>
</#macro>