<#macro main menu>
<div class="navbar navbar-inverse navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
		    </button>
		    <a class="navbar-brand" href="${rc.contextPath}/home" style="padding: 0px"><img src="${rc.contextPath}/resources/img/logo-40_150.png" style="border: 0px"></a>
		</div>
		<div class="collapse navbar-collapse navbar-ex1-collapse">
                 <ul class="nav navbar-nav">
                    <li<#if menu == "projects"> class="active"</#if>><a href="${rc.contextPath}/project">Projects</a></li>
                    <li<#if menu == "runs"> class="active"</#if>><a href="${rc.contextPath}/run">Runs</a></li>
                    <li<#if menu == "scripts"> class="active"</#if>><a href="${rc.contextPath}/script">Scripts</a></li>
                 </ul>
        </div>
	</div>
	</div>
</div>
</#macro>