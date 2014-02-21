<#import 'navigation.macro.ftl' as navigation>
<#import 'header.macro.ftl' as header>
<#import 'footer.macro.ftl' as footer>
<#assign customHeaders=""/>

<#-- Custom headers can be passed for each page using this layout -->
<#macro head>
	<#assign customHeaders>
		<#nested/>
	</#assign>
</#macro>

<#macro main title menu>
	<html>
	    <@header.main title customHeaders/>
	    <body>
			<!-- Main navigation -->
			<@navigation.main menu/>
			
			<!-- Templates -->
	        <div class="container">
	        	<#if error?exists>
	        		<div class="alert alert-danger">
	        			<strong>Error</strong> : ${error!}
	        		</div>
	        	</#if>
		    	<#nested/>
			</div>

			<@footer.main/>
	    </body>
	</html>

</#macro>