<#macro main menu>
<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="navbar-inner">
         <div class="container">
            <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="brand" href="${rc.contextPath}/home" style="padding: 0px"><img src="${rc.contextPath}/resources/img/logo-40_150.png" style="border: 0px"></a>
            <div class="nav-collapse collapse">
                 <ul class="nav">
                    <li<#if menu == "projects"> class="active"</#if>><a href="${rc.contextPath}/project">Projects</a></li>
                    <li<#if menu == "runs"> class="active"</#if>><a href="${rc.contextPath}/run">Runs</a></li>
                    <li<#if menu == "scripts"> class="active"</#if>><a href="${rc.contextPath}/script">Scripts</a></li>
                 </ul>
            </div>
         </div>
     </div>
</div>
</#macro>