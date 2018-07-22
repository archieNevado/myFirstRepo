<#ftl strip_whitespace=true>
<#import "util.ftl" as util>

<#-- -------------------------------------------------------------------------------------------------------------------
 *
 * Please check the section "CoreMedia FreeMarker API" in chapter "Reference" in the "Frontend Developer Guide" for
 * details and examples for the following directives.
 * Any changes, additions or removals need to be documented in the manual.
 *
 ------------------------------------------------------------------------------------------------------------------- -->


<#-- DEPRECATED, see Frontend Developer Guide -->
<#macro notification type baseClass="cm-notification" additionalClasses=[] title="" text="" dismissable=false iconClass="" attr={}>
  <#local classes=[baseClass, baseClass + "--" + type] + additionalClasses />
  <#local attr=util._extendSequenceInMap(attr, "classes", classes) />
  <div<@util.renderAttr attr=attr />>
    <#if iconClass?has_content>
      <i class="${iconClass}" aria-hidden="true"></i>
    </#if>
    <#if title?has_content>
      <span class="${baseClass}__headline">${title}</span>
    </#if>
    <span class="${baseClass}__text">
    <#if text?has_content>${text}</#if>
    <#nested />
    </span>
  </div>
</#macro>

<#-- DEPRECATED, see Frontend Developer Guide -->
<#outputformat "plainText">
  <#macro notificationFromSpring path baseClass="cm-notification" additionalClasses=[] ignoreIfEmpty=true type="error" title="" bindPath=true attr={}>
    <#if bindPath><@spring.bind path=path /></#if>
    <#local text="" />
    <#if spring.status.error>
      <#local text=spring.status.getErrorMessagesAsString("\n") />
    </#if>
    <#if !ignoreIfEmpty?is_boolean || !ignoreIfEmpty || text?has_content>
      <@notification type=type baseClass=baseClass additionalClasses=additionalClasses title=title attr=attr>${text?replace("\n", "<br>")}<#nested /></@notification>
    </#if>
  </#macro>
</#outputformat>
