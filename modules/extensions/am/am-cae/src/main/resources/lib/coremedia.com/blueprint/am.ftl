<#ftl strip_whitespace=true>
<#-- @ftlvariable name="amFreemarkerFacade" type="com.coremedia.blueprint.assets.cae.tags.AMFreemarkerFacade" -->

<#function getDownloadPortal>
  <#return amFreemarkerFacade.getDownloadPortal() />
</#function>

<#function hasDownloadPortal>
  <#return amFreemarkerFacade.hasDownloadPortal() />
</#function>

<#function getAssetType asset>
  <#return (asset.content.type.name)!"" />
</#function>
