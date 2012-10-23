<?xml version="1.0"?>
<xsl:stylesheet version="2.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fn="http://www.w3.org/2005/xpath-functions">

  <xsl:output method="html" encoding="iso-8859-1" indent="no"/>

  <xsl:template match="listing">
   <xsl:variable name="directory" select="@directory"/>
   <html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
      <title> Directory Listing For <xsl:value-of select="$directory"/></title>
      <link href="/styles/style.css" rel="stylesheet" type="text/css"/>
      <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
    </head>
    <body>
      <h1>
      	Directory listing for <xsl:value-of select="$directory"/>
      	<a href="{$directory}?as=zip" class="download-as-zip" title="Download this directory as a ZIP file" style="visibility: hidden">
	      <img src="/images/icons/compress.png" alt="ZIP" class="icon"/>
	    </a>
      </h1>
      <hr size="1" />
      <table>
        <thead>
          <tr>
            <th class="file-name-header">Filename</th>
            <th class="file-size-header">Size</th>
            <th class="file-last-modified-date-header">Last Modified</th>
          </tr>
        </thead>
        <tbody>
          <xsl:if test="not(fn:matches($directory, '^/[^/]+/[^/]+/[^/]+/$'))">
            <tr>
              <td class="file-name"><a href="../"><tt>../</tt></a></td>
              <td class="file-size"><tt></tt></td>
              <td class="file-last-modified-date"><tt></tt></td>
            </tr>
          </xsl:if>
          <xsl:apply-templates select="entries"/>
        </tbody>
      </table>
      <xsl:apply-templates select="readme"/>
      <script type="text/javascript">
	    // <![CDATA[
          $(document).ready(function() {
            $('h1, .file-name').hover(
              function() { $(this).find('.download-as-zip').css({'visibility': 'visible'}); },
              function() { $(this).find('.download-as-zip').css({'visibility': 'hidden'}); }
            );
          });
        //]]>
      </script>
    </body>
   </html>
  </xsl:template>

  <xsl:template match="entries">
    <xsl:apply-templates select="entry"/>
  </xsl:template>

  <xsl:template match="readme">
    <hr size="1" />
    <xsl:value-of select="." disable-output-escaping="yes" />
  </xsl:template>

  <xsl:template match="entry">
    <xsl:variable name="urlPath" select="fn:replace(@urlPath, '^.+/([^/+]/?)', '$1')"/>
    <xsl:if test="not(fn:matches($urlPath,'^\.(_|DS_Store)'))">
      <tr>
        <td class="file-name">
          <a href="{$urlPath}"><tt><xsl:apply-templates/></tt></a>
          <xsl:if test="not(fn:matches($urlPath, '\.(zip|jar|war|7z|rar|gz|bz2|Z|gif|jpe?g|png|bmp|mp3|aac|mov|mp4a?|mpeg|m4v|wmv|avi)', 'i'))">
	        <a href="{$urlPath}?as=zip" class="download-as-zip" title="Download as a ZIP file" style="visibility: hidden">
	          <img src="/images/icons/compress.png" alt="ZIP" class="icon"/>
	        </a>
          </xsl:if>
        </td>
        <td class="file-size">
          <tt><xsl:value-of select="@size"/></tt>
        </td>
        <td class="file-last-modified-date">
          <tt><xsl:value-of select="@date"/></tt>
        </td>
      </tr>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>