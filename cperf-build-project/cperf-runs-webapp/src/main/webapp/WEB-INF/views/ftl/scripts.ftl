<#import 'macros/layout.macro.ftl' as layout>
<#import "spring.ftl" as spring />

<@layout.main title="Scripts">
<table>
	<thead>
		<tr>
			<th>Label</th>
			<th>JMX</th>
			<th>Actions</th>
		</tr>
	</thead>
	
	<#list scripts as script>
		<tr>
			<td>${script.label}</td>
			<td><div class="scroll"><pre>${script.jmx?xml}</pre></div></td>
			<td>
				<a href="${rc.contextPath}/script/${script.id}/detail">detail</a>
				<a href="${rc.contextPath}/script/${script.id}/delete">delete</a>
		 	</td>
		</tr>
	</#list>
</table>

<h2>New Script</h2>
<form method="post" action="${rc.contextPath}/script/new" enctype="multipart/form-data">

	<table>
		<tr>
			<td><label for="label">Label</label></td>
			<td>
				<@spring.formInput "command.label"/>
				<@spring.showErrors "<br/>", "cssError"/>
			</td>
		</tr>
		<tr>
			<td><label for="jmxFile">JMX File</label></td>
			<td><input type="file" name="jmxFile" id="jmxFile" /></td>
		</tr>
		<tr>
			<td colspan="2"><input type="submit" value="New script" /></td>
		</tr>
	</table>

</form>

</@layout.main>