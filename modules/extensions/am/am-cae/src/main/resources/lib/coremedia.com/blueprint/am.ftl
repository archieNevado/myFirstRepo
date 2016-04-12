<#ftl strip_whitespace=true>
<#-- @ftlvariable name="amFreemarkerFacade" type="com.coremedia.blueprint.assets.cae.tags.AMFreemarkerFacade" -->

<#assign messageKeys=amFreemarkerFacade.getAmMessageKeys()/>

<#function getDownloadPortal>
  <#return amFreemarkerFacade.getDownloadPortal() />
</#function>

<#function hasDownloadPortal>
  <#return amFreemarkerFacade.hasDownloadPortal() />
</#function>

<#function localizeAssetMetadata key>
  <#return bp.getMessage(messageKeys.getMETADATA_PREFIX() + key) />
</#function>

<#function localizeRenditionName rendition>
  <#return bp.getMessage(messageKeys.getRENDITION_PREFIX() + rendition.name!"") />
</#function>

<#function getAssetType asset>
  <#return (asset.content.type.name)!"" />
</#function>
