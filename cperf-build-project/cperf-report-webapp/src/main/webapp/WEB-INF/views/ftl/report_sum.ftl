<#import 'macros/layout.macro.ftl' as layout>
<#import "spring.ftl" as spring />

<@layout.main title="Report">

<script type='text/javascript'>
	<#include "graphs/sum.ftl">
</script>
<div id='sumChart'></div>
<div id='Details' style="width: 400px;height: 130px;left: 50%;position: absolute;margin-left: -250px;">

<table width=100%>
	<tr height="35px">
		<td style="border:1px solid black" colspan="4" align="center"><b>Details<b></td>
	</tr>
	<tr>
		<td style="border:1px solid black" colspan="2" align="center"><b>Response Time (ms)<b></td>
		<td style="border:1px solid black" colspan="2" align="center"><b>Request / seconds<b></td>
	</tr>
	<tr>
		<td style="border:1px solid black" align="center"><b>min</b></td>
		<td style="border:1px solid black" align="right">${series.rstMin}</td>
		<td style="border:1px solid black" align="center"><b>min</b></td>
		<td style="border:1px solid black" align="right">${series.reqMin}</td>
	</tr>
	<tr>
		<td style="border:1px solid black" align="center"><b>max</b></td>
		<td style="border:1px solid black" align="right">${series.rstMax}</td>
		<td style="border:1px solid black" align="center"><b>max</b></td>
		<td style="border:1px solid black" align="right">${series.reqMax}</td>
	</tr>
	<tr>
		<td style="border:1px solid black" align="center"><b>avg</b></td>
		<td style="border:1px solid black" align="right">${series.rstAvg}</td>
		<td style="border:1px solid black" align="center"><b>avg</b></td>
		<td style="border:1px solid black" align="right">${series.reqAvg}</td>
	</tr>
</table>
</div>
</@layout.main>