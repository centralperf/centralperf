<#import 'navigation.macro.ftl' as navigation>
<#import 'header.macro.ftl' as header>
<#macro main title>

	<html>
	    <@header.main title/>
	    <body>
			<H1>${title}</H1>
			<!-- Main navigation -->
			<@navigation.main/>
			
			<!-- Templates -->
	        <#nested/>
	    </body>
	</html>

</#macro>