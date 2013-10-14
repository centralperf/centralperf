<#import "/spring.ftl" as spring />
<#macro main>
        <fieldset>
            <label for="jmxFile">File in JMETER CSV format (JTL)</label>
            <input type="file" name="jtlFile" id="jtlFile" class="input"/>
            <label></label><#-- bug in Twitter boostrap force to add empty label -->
        </fieldset>
</#macro>