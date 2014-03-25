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
<#macro main title customHeaders="">
	    <head>
	        <title>CENTRAL PERF - ${title}</title>

            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8">

            <#-- jQuery -->
	        <script src="${rc.contextPath}/resources/js/jquery.min.js"></script>
            <script src="${rc.contextPath}/resources/js/jquery-plugins/jquery.simplemodal.1.4.4.min.js"></script>

            <#-- Twitter Bootstrap -->
            <link rel="stylesheet" href="${rc.contextPath}/resources/css/bootstrap.min.css">
            <style>
                body {
                    padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
                }
            </style>
            <link href="${rc.contextPath}/resources/css/bootstrap-responsive.css" rel="stylesheet">
            <script src="${rc.contextPath}/resources/js/bootstrap.min.js"></script>

			<#-- Bootstrap select -->
			<link href="${rc.contextPath}/resources/css/bootstrap-select.min.css" rel="stylesheet">
      		<script src="${rc.contextPath}/resources/js/bootstrap-select.min.js"></script>
      		<script type="text/javascript">$(document).ready(function(){$('.selectpicker').selectpicker();});</script>
      		
      		<#-- Bootstrap editable -->
			<link href="${rc.contextPath}/resources/css/bootstrap-editable.css" rel="stylesheet">
      		<script src="${rc.contextPath}/resources/js/bootstrap-editable.min.js"></script>      		

			<#-- Moment JS -->
			<script src="${rc.contextPath}/resources/js/moment/moment-with-langs.min.js"></script>
			
            <#-- Central Perf -->
            <link rel="stylesheet" type="text/css" href="${rc.contextPath}/resources/css/main.css">
            <script src="${rc.contextPath}/resources/js/centralperfs.js"></script>
            <script>
                $( document ).ready(function() {
				    // Edit in place
				    setEditable('#projectNameEditable');
				    setEditable('#projectDescriptionEditable');
				    setEditable('#scriptLabelEditable');
				    setEditable('#scriptDescriptionEditable');
				    setEditable('#runLabelEditable');
				    setEditable('#runCommentEditable');
				});
            </script>
            
            ${customHeaders}
	    </head>
</#macro>
