<#ftl strip_whitespace=true>
<#-- @ftlvariable name="springMacroRequestContext" type="org.springframework.web.servlet.support.RequestContext" -->

<#-- --- DEPRECATED ------------------------------------------------------------------------------------------------ -->

<#-- DEPRECATED -->
<#-- LOCALIZED MESSAGE as function -->
<#function getMessage key args=[] highlightErrors=false>
  <#return cm.getMessage(key, args, highlightErrors) />
</#function>

<#-- DEPRECATED -->
<#-- LOCALIZED MESSAGE as macro -->
<#macro message key args=[] escaping=false highlightErrors=true>
  <@cm.message key args escaping highlightErrors />
</#macro>

<#-- DEPRECATED -->
<#-- CHECK MESSAGE KEY -->
<#function hasMessage key="">
  <#return cm.hasMessage(key) />
</#function>
