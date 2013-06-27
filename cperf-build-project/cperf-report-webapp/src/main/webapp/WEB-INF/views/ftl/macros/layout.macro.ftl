<#import 'navigation.macro.ftl' as navigation>
<#import 'header.macro.ftl' as header>
<#macro main title>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	    <@header.main title/>
	    <body>
	    	<table>
	    		<tr>
	    			<td><img src="${rc.contextPath}/resources/img/logo-150.png" alt="LOGO" /></td>
	    			<#if runs??>
						<td valign="middle"><H2>Select a run :</H2></td>
						<td valign="middle">
		    				<form name="Reportform" >
								<select size="1"  onChange="location = this.options[this.selectedIndex].value;">
									<option value="#" >Select a run</option>
									<#list runs as run>
										<option value="${rc.contextPath}/report/${run.id}/sum" >${run.label}</option>
									</#list>
								</select>
							</form>
						</td>
					<#else>
						<td><H2>Report for run ${run.label}</H2></td>
						<td><a href="${rc.contextPath}/">(change)</a></td>
					</#if>
	    		</tr>
	    	</table>
			<!-- Main navigation -->
			<@navigation.main/>
			
			<!-- Templates -->
	        <#nested/>
	    </body>
	</html>

</#macro>