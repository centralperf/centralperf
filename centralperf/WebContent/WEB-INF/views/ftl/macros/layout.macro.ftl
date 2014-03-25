<#--
  Copyright (C) 2014  The Central Perf authors
 
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as
  published by the Free Software Foundation, either version 3 of the
  License, or (at your option) any later version.
 
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.
 
  You should have received a copy of the GNU Affero General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->
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