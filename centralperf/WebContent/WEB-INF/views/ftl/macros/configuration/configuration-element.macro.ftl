<#macro main keyData group>
	<div class="form-group row">
			<label class="col-md-3 control-label" for="${keyData.keyName}" title="${keyData.keyToolTip}">${keyData.keyLabel}</label>
			
			<#if keyData.isBooleanValue()>
				<div class="btn-group">
					<#if "TRUE"==keyData.getKeyValue()?upper_case>
			    		<button type="button" class="btn btn-default" <#if keyData.isReadOnly()>disabled</#if>>Off</button>
			    		<button type="button" class="btn btn-success" <#if keyData.isReadOnly()>disabled</#if>>On</button>
			    	<#else>
			    		<button type="button" class="btn btn-danger"  <#if keyData.isReadOnly()>disabled</#if>>Off</button>
			    		<button type="button" class="btn btn-default" <#if keyData.isReadOnly()>disabled</#if>>On</button>
			    	</#if>
		  		</div>
			<#else>
				<div class="col-md-9 input-group">
					<#if keyData.isReadOnly()>
						<input type="text" id="${keyData.keyName}" value="${keyData.keyValue}" class="form-control" disabled/>
					<#else>
						<input type="text" id="${keyData.keyName}" value="${keyData.keyValue}" defaultvalue="${keyData.keyValue}" class="form-control cpConfInput"/>
					</#if>

					<span id="revert-${keyData.keyName}" class="input-group-addon" <#if !keyData.isFromDb()>style="display: none;"</#if>>
						<a href="${rc.contextPath}/configuration/revert/${keyData.keyName}/${group}" title "Revert to properties value (delete from database)">
							<span style="color: #F00;" class="glyphicon glyphicon-share"></span>
						</a>
					</span>
											
					<span class="input-group-addon">
						<#if keyData.isReadOnly()>
							<span class="glyphicon glyphicon-eye-open"></span>
						<#else>
							<span class="glyphicon glyphicon-pencil"></span>
						</#if>
					</span>
				</div>
			</#if>
	</div> 
</#macro>