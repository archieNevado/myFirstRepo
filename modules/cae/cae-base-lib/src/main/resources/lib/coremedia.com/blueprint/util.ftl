<#ftl strip_whitespace=true>

<#-- -------------------------------------------------------------------------------------------------------------------
 *
 * Please check the section "CoreMedia FreeMarker API" in chapter "Reference" in the "Frontend Developer Guide" for
 * details and examples for the following directives.
 * Any changes, additions or removals need to be documented in the manual.
 *
 ------------------------------------------------------------------------------------------------------------------- -->


<#-- ATTRIBUTES -->
<#macro renderAttr attr={} ignore=[]>
  <#if attr?keys?seq_contains("classes") && !ignore?seq_contains("classes")>
    <#local classes=attr["classes"] />
    <#if attr?keys?seq_contains("class")>
      <#local classes=classes + attr["class"]?replace("  ", " ")?split(" ") />
    </#if>
    <#local attr=attr + {"class": classes?join(" ")} />
  </#if>
  <#local ignore=ignore + ["classes"] />
  <#list attr?keys as key><#if !ignore?seq_contains(key)><#assign value=attr[key]/><#if key=="metadata"><@preview.metadata data=value /><#elseif key?contains("data-")><@cm.dataAttribute name=key data=value/><#elseif value?has_content> ${key}="${value}"</#if></#if></#list>
</#macro>

<#-- TIME ELEMENT -->
<#macro renderDate contentDate cssClass="">
  <#if contentDate?has_content>
    <time class="${cssClass}" datetime="${contentDate?datetime?string.iso}"<@preview.metadata "properties.externallyDisplayedDate" />>${contentDate?date?string.medium}</time>
  </#if>
</#macro>

<#-- LOCALIZED MESSAGE -->
<#outputformat "plainText">
  <#macro message key args=[] highlightErrors=true>
    <#compress>
      <#if key?has_content>
        <#local messageText><@spring.messageText key key/></#local>
        <#if messageText?has_content && messageText != key>
          <#if args?has_content>
            <#local messageText><@spring.messageArgs key args/></#local>
          </#if>
          ${messageText}
        <#elseif ((cmpage.developerMode)!false) && highlightErrors>
          <span title="key '${key}' is missing" class="cm-preview-missing-key">[---${key}---]</span>
        <#else>
          [---${key}---]
        </#if>
      </#if>
    </#compress>
  </#macro>
</#outputformat>

<#-- LOCALIZED MESSAGE -->
<#outputformat "plainText">
  <#function getMessage key args=[] highlightErrors=false>
    <#local result><@message key=key args=args highlightErrors=highlightErrors /></#local>
    <#return result />
  </#function>
</#outputformat>

<#-- CHECK MESSAGE KEY -->
<#function hasMessage key>
  <#local messageText><@spring.messageText key ""/></#local>
  <#return messageText?has_content />
</#function>

<#-- OPTIONAL LINK -->
<#macro optionalLink href attr={} render=true>
  <#if render && href?has_content><a href="${href}"<@renderAttr attr />></#if>
  <#nested>
  <#if href?has_content></a></#if>
</#macro>

<#-- OPTIONAL CONTAINER -->
<#macro optionalFrame title="" classFrame="" attr={} attrTitle={}>
  <#if title?has_content>
    <div class="cm-frame ${classFrame}"<@renderAttr attr />>
      <h2 class="cm-frame__title"<@renderAttr attrTitle />>${title}</h2>
  </#if>
    <#nested>
  <#if title?has_content>
    </div>
  </#if>
</#macro>

<#-- LINE BREAKS -->
<#macro renderWithLineBreaks text>
  <#noautoesc>
    ${text?trim?replace("\n\n", "<br>")?replace("\n", "<br>")}
  </#noautoesc>
</#macro>

<#-- GET CSS CLASS NAME OF GIVEN IDENTIFIER -->
<#function asCSSClassName identifier>
  <#local cleanedUpIdentifier=identifier?replace("[!\"#$%&'\\(\\)\\*\\+,\\.\\/:;<=>\\?@\\[\\\\\\]\\^`\\{\\|}~\\s]", "", "r") />) />
  <#return cleanedUpIdentifier?replace("([A-Z][a-z])|([A-Z]$)", " $0", "r")?lower_case?replace(" ", "-") />
</#function>


<#-- --- PRIVATE --------------------------------------------------------------------------------------------------- -->


<#-- PRIVATE -->
<#function _extendSequenceInMap map={} key="" extendBy=[]>
  <#local newSequence=extendBy />
  <#if map?keys?seq_contains(key) && map[key]?is_sequence>
    <#local newSequence=map[key] + extendBy />
  </#if>
  <#return map + {key: newSequence} />
</#function>


<#-- --- DEPRECATED ------------------------------------------------------------------------------------------------ -->


<#-- DEPRECATED, UNUSED -->
<#macro optionalFrameForObject self="" classFrame="" attr={}>
  <#if self.teaserTitle?has_content  || self.teaserText?has_content>
  <div class="cm-frame ${classFrame}"<@renderAttr attr />>
    <#if self.teaserTitle?has_content>
        <h2 class="cm-frame__title"<@preview.metadata ["properties.teaserTitle", self.content] />>${self.teaserTitle}</h2>
    </#if>
  </#if>
  <#nested>
  <#if self.teaserTitle?has_content  || self.teaserText?has_content>
    <#if self.teaserText?has_content>
        <div class="cm-frame__text"<@preview.metadata ["properties.teaserTitle", self.content] />><@cm.include self=self.teaserText/></div>
    </#if>
  </div>
  </#if>
</#macro>
