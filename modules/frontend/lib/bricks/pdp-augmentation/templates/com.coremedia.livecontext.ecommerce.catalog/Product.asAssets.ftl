<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.catalog.Product" -->

<@cm.include self=self view="asDynaAssets" />

<#-- load inline javascript for IBM WCS -->
<#if lc.getVendorName() == 'IBM'>
  <@cm.include self=self view="_inlineScriptForWCS" />
</#if>

