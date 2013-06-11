<#import 'macros/layout.macro.ftl' as layout>
<#import "spring.ftl" as spring />

<@layout.main title="Runs">
	<table>
		<thead>
			<tr>
				<th>Label</th>
				<th>Launched</th>
				<th>Running</th>
				<th>Script</th>
				<th>Actions</th>
			</tr>
		</thead>
		
		<#list runs as run>
			<tr>
				<td>${run.label}</td>
				<td>${run.launched?string} <#if run.launched>(${run.startDate?string})</#if></td>
				<td>${run.running?string}</td>
				<td>
					<a href="${rc.contextPath}/script/${run.script.id}/edit">${run.script.label}</a>
				</td>
				<td>
					<a href="${rc.contextPath}/run/${run.id}/detail">detail</a> -
			 		<a href="${rc.contextPath}/run/${run.id}/delete">delete</a>
			 		<#if !run.running>
			 			-
			 			<a href="${rc.contextPath}/run/${run.id}/launch">launch <#if run.launched && !run.running>again</#if></a>
			 		</#if>
			 	</td>
			</tr>		 
		</#list>
	</table>
	<form method="post" action="${rc.contextPath}/run/new">
		<table>
			<@spring.bind "command.label" />
			<tr>
				<td><label for="${spring.status.expression}">Label</label></td>
				<td>
					<@spring.formInput "command.label"/>
					<@spring.showErrors "<br/>", "cssError"/>
				</td>
			</tr>
			<@spring.bind "command.script.id" />
			<tr>
				<td><label for="${spring.status.expression}">Script</label></td>
				<td><select name="${spring.status.expression}">
						<#list scripts as script>
							<option value="${script.id}">${script.label}
						</#list>
					</select>
				</td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit" value="New run" /></td>
			</tr>
		</table>
	</form>
</@layout.main>