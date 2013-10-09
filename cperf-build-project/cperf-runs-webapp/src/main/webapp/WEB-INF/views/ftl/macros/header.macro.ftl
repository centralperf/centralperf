<#macro main title>
	    <head>
	        <title>CENTRAL PERF - ${title}</title>

            <meta name="viewport" content="width=device-width, initial-scale=1.0">

            <#-- jQuery -->
	        <script src="${rc.contextPath}/resources/js/jquery.min.js"></script>
            <script src="${rc.contextPath}/resources/js/jquery.simplemodal.1.4.4.min.js"></script>

            <#-- Twitter Bootstrap -->
            <link rel="stylesheet" href="${rc.contextPath}/resources/css/bootstrap.min.css">
            <style>
                body {
                    padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
                }
            </style>
            <link href="${rc.contextPath}/resources/css/bootstrap-responsive.css" rel="stylesheet">
            <script src="${rc.contextPath}/resources/js/bootstrap.min.js"></script>


            <#-- Central Perf -->
            <link rel="stylesheet" type="text/css" href="${rc.contextPath}/resources/css/main.css">
            <script src="${rc.contextPath}/resources/js/centralperfs.js"></script>
            <script>
                $( document ).ready(function() {
                    $("#newRun").click(function(){displayModal("${rc.contextPath}/project/" + $(this).attr("projectId") + "/run/new")});
                    $("#newProject").click(function(){displayModal("${rc.contextPath}/project/new")});
                    $("#newScript").click(function(){displayModal("${rc.contextPath}/project/" + $(this).attr("projectId") + "/script/new")});
                });
            </script>
	    </head>
</#macro>