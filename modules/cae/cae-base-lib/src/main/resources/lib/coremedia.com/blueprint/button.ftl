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
<#macro button text="" href="" baseClass="cm-button" iconClass="" iconText="" textClass="" attr={}>
  <#if href?has_content>
    <#if iconClass?has_content && text?has_content>
      <@_linkWithIconAndText href=href baseClass=baseClass icon=true text=text iconClass=iconClass iconText=iconText textClass=textClass attr=attr />
    <#elseif iconClass?has_content>
      <@_linkWithIcon href=href baseClass=baseClass iconClass=iconClass iconText=iconText attr=attr />
    <#else>
      <@_linkWithText href=href baseClass=baseClass text=text textClass=textClass attr=attr />
    </#if>
  <#else>
    <#if iconClass?has_content && text?has_content>
      <@_buttonWithIconAndText baseClass=baseClass icon=true text=text iconClass=iconClass iconText=iconText textClass=textClass attr=attr />
    <#elseif iconClass?has_content>
      <@_buttonWithIcon baseClass=baseClass iconClass=iconClass iconText=iconText attr=attr />
    <#else>
      <@_buttonWithText baseClass=baseClass text=text textClass=textClass attr=attr />
    </#if>
  </#if>
</#macro>


<#-- --- PRIVATE --------------------------------------------------------------------------------------------------- -->


<#--
 * Renders an icon and text as <a> tag. Not to be used outside of this template, use #macro button instead.
 * PRIVATE
 -->
<#macro _linkWithIconAndText href baseClass icon text iconClass iconText="" textClass="" attr={}>
  <#local hasText=text?is_string && text?has_content />
  <#local hasIcon=icon?is_boolean && icon />
  <#local iconClasses=[] />
  <#local textClasses=[] />
  <#if baseClass?has_content>
    <#local buttonClasses=[baseClass] />
    <#-- add modifier to determine if text and icons exists -->
    <#if hasText>
      <#local buttonClasses=buttonClasses + [baseClass + "--with-text"] />
    </#if>
    <#if hasIcon>
      <#local buttonClasses=buttonClasses + [baseClass + "--with-icon"] />
    </#if>
    <#local attr=util._extendSequenceInMap(attr, "classes", buttonClasses) />
    <#local iconClasses=iconClasses + [baseClass + "__icon"] />
    <#local textClasses=textClasses + [baseClass + "__text"] />
  </#if>
  <#if iconClass?has_content>
    <#local iconClasses=iconClasses + [iconClass] />
  </#if>
  <#if textClass?has_content>
    <#local textClasses=textClasses + [textClass] />
  </#if>
<a href="${href}"<@util.renderAttr attr, ["href"] />>
  <#if hasIcon><i<@util.renderAttr attr={"classes": iconClasses} />><#if !hasText><span class="cm-visuallyhidden">${iconText}</span></#if></i></#if>
  <#if hasText><span<@util.renderAttr attr={"classes": textClasses} />>${text}</span></#if>
</a>
</#macro>

<#--
 * Renders an icon as <a> tag. Not to be used outside of this template, use #macro button instead.
 * PRIVATE
 -->
<#macro _linkWithIcon href baseClass iconClass iconText="" attr={}>
  <@_linkWithIconAndText baseClass=baseClass href=href icon=true text="" iconClass=iconClass iconText=iconText attr=attr />
</#macro>

<#--
 * Renders text as <a> tag. Not to be used outside of this template, use #macro button instead.
 * PRIVATE
 -->
<#macro _linkWithText href baseClass text textClass="" attr={}>
  <@_linkWithIconAndText baseClass=baseClass href=href icon=false text=text iconClass="" textClass=textClass attr=attr />
</#macro>

<#--
 * Renders an icon and text as <button> tag. Not to be used outside of this template, use #macro button instead.
 * PRIVATE
 -->
<#macro _buttonWithIconAndText baseClass icon text iconClass="" iconText="" textClass="" attr={}>
  <#local hasText=text?is_string && text?has_content />
  <#local hasIcon=icon?is_boolean && icon />
  <#local attr={"type": "button"} + attr />
  <#local iconClasses=[] />
  <#local textClasses=[] />
  <#if baseClass?has_content>
    <#local buttonClasses=[baseClass] />
    <#-- add modifier to determine if text and icons exists -->
    <#if hasText>
      <#local buttonClasses=buttonClasses + [baseClass + "--with-text"] />
    </#if>
    <#if hasIcon>
      <#local buttonClasses=buttonClasses + [baseClass + "--with-icon"] />
    </#if>
    <#local attr=util._extendSequenceInMap(attr, "classes", buttonClasses) />
    <#local iconClasses=iconClasses + [baseClass + "__icon"] />
    <#local textClasses=textClasses + [baseClass + "__text"] />
  </#if>
  <#if iconClass?has_content>
    <#local iconClasses=iconClasses + [iconClass] />
  </#if>
  <#if textClass?has_content>
    <#local textClasses=textClasses + [textClass] />
  </#if>
<button<@util.renderAttr attr />>
  <#if hasIcon><i<@util.renderAttr attr={"classes": iconClasses} />><#if !hasText><span class="cm-visuallyhidden">${iconText}</span></#if></i></#if>
  <#if hasText><span<@util.renderAttr attr={"classes": textClasses} />>${text}</span></#if>
</button>
</#macro>

<#--
 * Renders an icon as <button> tag. Not to be used outside of this template, use #macro button instead.
 * PRIVATE
 -->
<#macro _buttonWithIcon baseClass iconClass iconText="" attr={}>
  <@_buttonWithIconAndText baseClass=baseClass icon=true text="" iconClass=iconClass iconText=iconText attr=attr />
</#macro>

<#--
 * Renders text as <button> tag. Not to be used outside of this template, use #macro button instead.
 * PRIVATE
 -->
<#macro _buttonWithText baseClass text textClass="" attr={}>
  <@_buttonWithIconAndText baseClass=baseClass icon=false text=text textClass=textClass attr=attr />
</#macro>
