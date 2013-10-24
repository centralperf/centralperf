<#macro main>
	<legend>Graphs</legend>
	<ul class="nav nav-tabs">
  		<li class="active"><a href="#">Summary</a></li>
  		<li><a href="#">Response time</a></li>
  		<li><a href="#">Request size</a></li>
  		<li><a href="#">Error rate</a></li>
  		<li style="float:right;margin-left:10px;margin-right:0;" ><a href="#"  class="label label-danger">logs</a></li>
  		<li style="float:right;margin-left:10px;margin-right:0;" ><a href="#"  class="label label-info">samples (${run.samples?size})</a></li>
	</ul>
	<#if page=="logs">
		<#include "/graphs/logs.ftl">
	<#else>
		<#include "/graphs/sum.ftl">
	</#if>
</#macro>