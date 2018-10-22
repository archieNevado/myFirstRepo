<#ftl strip_whitespace=true>
<#-- @ftlvariable name="springMacroRequestContext" type="org.springframework.web.servlet.support.RequestContext" -->

<#-- -------------------------------------------------------------------------------------------------------------------
 *
 * Please check the section "CoreMedia FreeMarker API" in chapter "Reference" in the "Frontend Developer Guide" for
 * details and examples for the following directives.
 * Any changes, additions or removals need to be documented in the manual.
 *
 ------------------------------------------------------------------------------------------------------------------- -->

<#-- LOCALIZED MESSAGE as function -->
<#function getMessage key args=[] highlightErrors=false>
  <#-- get message text with args -->
  <#if args?has_content>
    <#local messageText=springMacroRequestContext.getMessage(key, args, "")/>
  <#-- get message text -->
  <#else>
    <#local messageText=springMacroRequestContext.getMessage(key, "")/>
  </#if>
  <#-- if developerMode is enabled, show error message -->
  <#if !messageText?has_content && (cmpage.developerMode)!false>
    <#if highlightErrors>
      <#local messageText><span title="key '${key}' is missing" class="cm-preview-missing-key">Translation for key '${key}' is missing in the ResourceBundle.</span></#local>
    <#else>
      <#local messageText="Translation for key '${key}' is missing in the ResourceBundle." />
    </#if>
  </#if>
  <#return messageText!"" />
</#function>

<#-- LOCALIZED MESSAGE as macro -->
<#macro message key args=[] escaping=false highlightErrors=true>
  <#compress>
    <#if escaping>
      ${getMessage(key, args, highlightErrors)}
    <#else>
      ${getMessage(key, args, highlightErrors)?no_esc}
    </#if>
  </#compress>
</#macro>

<#-- CHECK MESSAGE KEY -->
<#function hasMessage key="">
  <#return springMacroRequestContext.getMessage(key, "")?has_content />
</#function>
