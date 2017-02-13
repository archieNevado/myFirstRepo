<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMImageMap" -->

<#--
This template is reusing the imagemap brick. Therefore all rendering is taking place in e.g. CMImageMap.hero.ftl.

The following parameters can be set:
 - additionalClass (String): to set a specific CSS class. It automatically defaults to "cm-hero"
 - renderTeaserText, default: true
 - renderCTA, default true
 - renderDimmer, default true
 - renderEmptyImage, default true
 - "imagemap_use_quickinfo, default true
-->

<@cm.include self=self view="hero" params={"imagemap_use_quickinfo": false}/>
