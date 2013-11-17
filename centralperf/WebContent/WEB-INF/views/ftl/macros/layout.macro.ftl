<#import 'navigation.macro.ftl' as navigation>
<#import 'header.macro.ftl' as header>
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
	            <#nested/>
            </div>
	    </body>
	</html>

</#macro>