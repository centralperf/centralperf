<#import 'navigation.macro.ftl' as navigation>
<#import 'header.macro.ftl' as header>
<#macro main title menu>

	<html>
	    <@header.main title/>
	    <body>
			<!-- Main navigation -->
			<@navigation.main menu/>

			<!-- Templates -->
            <div class="container">
	            <#nested/>
            </div>
	    </body>
	</html>

</#macro>