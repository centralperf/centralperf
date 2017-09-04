<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


<xsl:template match="/">
	 <ul>
	 	<xsl:apply-templates></xsl:apply-templates>	
	 </ul>
</xsl:template>

<xsl:template match="dependency">
  <li>
  	<xsl:value-of select="groupId"/>.<xsl:value-of select="artifactId "/>(<xsl:value-of select="version"></xsl:value-of>)
  	<a href="{licenses/license/url}">
  		<xsl:value-of select="licenses/license/name"/>
  	</a>
  </li>
</xsl:template>

</xsl:stylesheet> 