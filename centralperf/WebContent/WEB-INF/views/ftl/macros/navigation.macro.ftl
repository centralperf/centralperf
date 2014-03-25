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
<#macro main menu>
<div class="navbar navbar-inverse navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container">
		<div class="navbar-header">
			<#if menu != "bootstrap">
				<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
			    </button>
		    </#if>
		    <a class="navbar-brand" href="${rc.contextPath}/home" style="padding: 0px"><img src="${rc.contextPath}/resources/img/logo-40_150.png" style="border: 0px"></a>
		</div>
		<#if menu != "bootstrap">
		<div class="collapse navbar-collapse navbar-ex1-collapse">
                 <ul class="nav navbar-nav">
                    <li<#if menu == "projects"> class="active"</#if>><a href="${rc.contextPath}/project">Projects</a></li>
                    <li<#if menu == "runs"> class="active"</#if>><a href="${rc.contextPath}/run">Runs</a></li>
                    <li<#if menu == "scripts"> class="active"</#if>><a href="${rc.contextPath}/script">Scripts</a></li>
                    <li<#if menu == "configuration"> class="active"</#if>><a href="${rc.contextPath}/configuration">Configuration</a></li>
                 </ul>
        </div>
        </#if>
	</div>
	</div>
</div>
</#macro>