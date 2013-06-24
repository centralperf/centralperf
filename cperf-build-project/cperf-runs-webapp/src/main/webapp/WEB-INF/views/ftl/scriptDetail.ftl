<#import 'macros/layout.macro.ftl' as layout>
<#import "spring.ftl" as spring />

<@layout.main title="Run detail">
	<H1>Script detail</H1>
	<ul>
		<li>Name : ${script.label}
		<li>JMX : <div class="scroll"><pre>${script.jmx?xml}</pre></div></pre>
		<li>Script variables
			<ul>
			<#list script.scriptVariableSets as variableSet>
			<li>${variableSet.name}
				<table>
				<thead>
					<tr>
						<th>Name</th>
						<th>Default value</th>
						<th>Description</th>
					</tr>
				</thead>
				
				<#list variableSet.scriptVariables as variable>
					<tr>
						<td>${variable.name}</td>
						<td>${variable.defaultValue}</td>
						<td>${variable.description!}</td>
					</tr>		 
				</#list>
				</table>
			</#list>
			</ul>
	</ul>
</@layout.main>