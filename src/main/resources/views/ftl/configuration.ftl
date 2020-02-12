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

<#import 'macros/layout.macro.ftl' as layout/>
<#import 'macros/configuration/configuration-element.macro.ftl' as conf_elem>
<#import "spring.ftl" as spring />

<@layout.main title="Options" menu="options">

<script type='text/javascript'>
 $(function () {
      var $input = $('.cpConfInput').change(function () {
      	inputObj=this;
      	revertSpan=document.getElementById("revert-"+this.id);
      	$.ajax({
			type: "POST",
			url: "${rc.contextPath}/configuration/update",
			data: {
				keyName : this.id,
				keyValue : this.value
			},
			success: function(msg){
        		$('#alert_template').find("span").html("<b>"+$(inputObj).attr('id')+"</b> succesfully updated to: <b>"+$(inputObj).val()+"</b><br/>(old value:"+$(inputObj).attr('defaultValue')+")");
        		$('#alert_template').attr("class", "conf-alert alert alert-success");
        		$(inputObj).attr("defaultValue",$(inputObj).val());
        		$(revertSpan).attr("style","");
  			},
  			error: function(XMLHttpRequest, textStatus, errorThrown) {
        		$('#alert_template').find("span").html("<b>"+$(inputObj).attr('id')+"</b> not updated to: <b>"+$(inputObj).val()+"</b><br/>(Still use old value:"+$(inputObj).attr('defaultValue')+")");
        		$('#alert_template').attr("class", "conf-alert alert alert-danger");
        		$(inputObj).val($(inputObj).attr('defaultValue'));
  			}
		});
		$('#alert_template').fadeIn('slow');
		$('#alert_template').delay(200).fadeIn().delay(4000).fadeOut();
      }); 
  })
</script>


    <legend style="line-height: 40px">Options  </legend>
   
	<div class="conf-alert " id="alert_template" style="display: none;">
    	<span>TEXT HERE</span>
	</div>
	
    <div class="panel-group" id="options-accordion">

  		<div class="panel panel-default">
    		<div class="panel-heading">
      			<h4 class="panel-title"><a data-toggle="collapse" data-parent="#options-accordion" href="#opt-general"><span class="glyphicon glyphicon-wrench"></span> General</a></h4>
    		</div>
    		<div id="opt-general" class="panel-collapse collapse <#if group==0>in</#if>">
      			<div class="panel-body">
      			
      			
					<div id="dbProperties" class="row">
						<div class="col-md-12">
					    	<form class="form-horizontal" role="form">
					             <@conf_elem.main config.getConfigurationData("server.mode") 0 />
					        </form>
					    </div>
					</div>  
					    			
      			</div>
    		</div>
  		</div>

  		<div class="panel panel-default">
    		<div class="panel-heading">
      			<h4 class="panel-title"><a data-toggle="collapse" data-parent="#options-accordion" href="#opt-runreport"><span class="glyphicon glyphicon-file"></span> Runs & Reports</a></h4>
    		</div>
    		<div id="opt-runreport" class="panel-collapse collapse <#if group==1>in</#if>">
      			<div class="panel-body">
					<div id="dbProperties" class="row">
						<div class="col-md-12">
					    	<form class="form-horizontal" role="form">
					             <@conf_elem.main config.getConfigurationData("csv.field_separator") 1/>
					             <@conf_elem.main config.getConfigurationData("report.cache.delay.seconds") 1/>
					        </form>
					    </div>
					</div>
      			</div>
    		</div>
  		</div>  
  		
  		<div class="panel panel-default">
    		<div class="panel-heading">
      			<h4 class="panel-title"><a data-toggle="collapse" data-parent="#options-accordion" href="#opt-database"><span class="glyphicon glyphicon-flash"></span> Database</a></h4>
    		</div>
    		<div id="opt-database" class="panel-collapse collapse">
      			<div class="panel-body">
					<div id="dbProperties" class="row">
						<div class="col-md-12">
					    	<form class="form-horizontal" role="form">
					             <@conf_elem.main config.getConfigurationData("db.driver") 2/>
					             <@conf_elem.main config.getConfigurationData("db.url") 2/>
					             <@conf_elem.main config.getConfigurationData("db.login") 2/>
					             <@conf_elem.main config.getConfigurationData("db.sqlDialect") 2/>
					        </form>
					    </div>
					</div>
      			</div>
    		</div>
  		</div>  

  		<div class="panel panel-default">
    		<div class="panel-heading">
      			<h4 class="panel-title"><a data-toggle="collapse" data-parent="#options-accordion" href="#opt-plg-jmeter"><span class="glyphicon glyphicon-cog"></span> Plugin JMeter</a></h4>
    		</div>
    		<div id="opt-plg-jmeter" class="panel-collapse collapse">
      			<div class="panel-body">
					<div id="jmeterProperties" class="row">
						<div class="col-md-12">
					    	<form class="form-horizontal" role="form">
					    		<@conf_elem.main config.getConfigurationData("jmeter.launcher.script.path") 3/>
					            <@conf_elem.main config.getConfigurationData("jmeter.launcher.output.csv.default_headers") 3/>
					            <@conf_elem.main config.getConfigurationData("jmeter.launcher.output.format") 3/>
					        </form>
					    </div>
					</div>
      			</div>
    		</div>
  		</div>

  		<div class="panel panel-default">
    		<div class="panel-heading">
      			<h4 class="panel-title"><a data-toggle="collapse" data-parent="#options-accordion" href="#opt-plg-gatling"><span class="glyphicon glyphicon-cog"></span> Plugin Gatling</a></h4>
    		</div>
    		<div id="opt-plg-gatling" class="panel-collapse collapse">
      			<div class="panel-body">
					<div id="GatlingProperties" class="row">
						<div class="col-md-12">
					    	<form class="form-horizontal" role="form">
					    		 <@conf_elem.main config.getConfigurationData("gatling.launcher.path") 4/>
					             <@conf_elem.main config.getConfigurationData("gatling.launcher.script.relativepath") 4/>
					             <@conf_elem.main config.getConfigurationData("gatling.launcher.output.log.default_headers") 4/>
					             <@conf_elem.main config.getConfigurationData("gatling.launcher.output.format") 4/>
					        </form>
					    </div>
					</div>
      			</div>
    		</div>
  		</div>  		
</div>
</@layout.main>
