<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" omit-xml-declaration="yes" />

	<!-- Stylesheet to display the basic details of a JMX test plan -->
	<xsl:template match="jmeterTestPlan">
				<script>
					$(document).ready(function() {
					$("#treeview").jstree({"plugins" : [ "themes"]});
					$("#treeview").jstree("open_all");
					});
				</script>
				<div id="treeview">
					<ul>
						<xsl:apply-templates />
					</ul>
				</div>
	</xsl:template>

	<xsl:template match="hashTree">
		<!-- First node -->
		<xsl:if test="not(preceding-sibling::*[1])">
			<xsl:apply-templates />
		</xsl:if>
		<xsl:if test="preceding-sibling::*[1]">
			<li>
				<xsl:apply-templates select="preceding-sibling::*[1]" />
				<xsl:if test="child::*">
					<ul>
						<xsl:for-each select="hashTree">
							<xsl:apply-templates select="." />
						</xsl:for-each>
					</ul>
				</xsl:if>
			</li>
		</xsl:if>
	</xsl:template>

	<xsl:template match="TestPlan">
		<xsl:call-template name="header" />
		<xsl:call-template name="comment" />
		<xsl:for-each select='elementProp/collectionProp/elementProp'>
			<xsl:value-of select='stringProp[@name="Argument.name"]' />
			<xsl:value-of select='stringProp[@name="Argument.metadata"]' />
			<xsl:value-of select='stringProp[@name="Argument.value"]' />
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="ThreadGroup">
		<xsl:call-template name="header" />
		<xsl:call-template name="comment" />
		(
		<xsl:text>Threads: </xsl:text>
		<xsl:value-of select='stringProp[@name="ThreadGroup.num_threads"]' />
		<xsl:text> Loops: </xsl:text>
		<xsl:value-of select='elementProp/*[@name="LoopController.loops"]' />
		<xsl:text> Ramp up: </xsl:text>
		<xsl:value-of select='stringProp[@name="ThreadGroup.ramp_time"]' />
		)
	</xsl:template>

	<xsl:template
		match="HTTPSampler|HTTPSampler2|ConfigTestElement[@guiclass='HttpDefaultsGui']">
		<xsl:call-template name="header" />
		<xsl:call-template name="comment" />
		(
		<xsl:value-of select='stringProp[@name="HTTPSampler.method"]' />
		<xsl:text> </xsl:text>
		<xsl:value-of select='stringProp[@name="HTTPSampler.protocol"]' />
		<xsl:text>://</xsl:text>
		<xsl:value-of select='stringProp[@name="HTTPSampler.domain"]' />
		<xsl:text>:</xsl:text>
		<xsl:value-of select='stringProp[@name="HTTPSampler.port"]' />
		<xsl:text>/</xsl:text>
		<xsl:value-of select='stringProp[@name="HTTPSampler.path"]' />
		)
	</xsl:template>

	<xsl:template match="ResultCollector">
		<xsl:call-template name="header" />
		<xsl:call-template name="comment" />
		<xsl:if test='stringProp[@name="filename"]!=""'>
			<br />
			Output:
			<xsl:value-of select='stringProp[@name="filename"]' />
			XML:
			<xsl:value-of select='objProp/value/xml' />
		</xsl:if>
	</xsl:template>

	<xsl:template match="*">
		<xsl:call-template name="header" />
		<xsl:call-template name="comment" />
	</xsl:template>

	<xsl:template name="comment">
		<xsl:if test='stringProp/@name="TestPlan.comments"'>
			<i>
				<xsl:value-of select='stringProp[@name="TestPlan.comments"]' />
			</i>
		</xsl:if>
	</xsl:template>

	<xsl:template name="header">
		<xsl:if test="@enabled = 'false'">
			(
		</xsl:if>
		<b>
			<xsl:choose>
				<xsl:when test="name() = 'GenericController'">
					<xsl:text>SimpleController</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="name()" />
				</xsl:otherwise>
			</xsl:choose>
		</b>
		:
		<xsl:value-of select="@testname" />
		<xsl:if test="@enabled = 'false'">
			)
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>