<#ftl strip_whitespace=true>
<#-- @ftlvariable name="blueprintFreemarkerFacade" type="com.coremedia.blueprint.cae.web.taglib.BlueprintFreemarkerFacade" -->
<#function translatedPropertyValue linkable locale fallback=cm.UNDEFINED>
  <#return blueprintFreemarkerFacade.getTranslatedPropertyValue(linkable, locale, fallback)>
</#function>
