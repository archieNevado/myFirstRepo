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


<#-- DEPRECATED, see Frontend Developer Guide -->
<#macro renderAttr attr={} ignore=[]>
  <#if attr?keys?seq_contains("classes") && !ignore?seq_contains("classes")>
    <#local classes=attr["classes"] />
    <#if attr?keys?seq_contains("class")>
      <#local classes=classes + attr["class"]?replace("  ", " ")?split(" ") />
    </#if>
    <#local attr=attr + {"class": classes?join(" ")} />
  </#if>
  <#local ignore=ignore + ["classes"] />
  <#list attr?keys as key><#if !ignore?seq_contains(key)><#local value=attr[key]/><#if key=="metadata"><@preview.metadata data=value /><#elseif key?contains("data-")><@cm.dataAttribute name=key data=value/><#elseif value?has_content> ${key}="${value}"</#if></#if></#list>
</#macro>

<#-- DEPRECATED, see Frontend Developer Guide -->
<#macro renderDate contentDate cssClass="" metadata=["properties.externallyDisplayedDate"]>
  <#if contentDate?has_content>
    <time class="${cssClass}" datetime="${contentDate?datetime?string.iso}"<@preview.metadata data=metadata />>${contentDate?date?string.medium}</time>
  </#if>
</#macro>

<#-- DEPRECATED, see Frontend Developer Guide -->
<#macro renderWithLineBreaks text>
  <#noautoesc>
    ${text?trim?replace("\n\n", "<br>")?replace("\n", "<br>")}
  </#noautoesc>
</#macro>

<#-- DEPRECATED, see Frontend Developer Guide -->
<#macro optionalLink href attr={} render=true>
  <#if render && href?has_content><a href="${href}"<@renderAttr attr />></#if>
  <#nested>
  <#if href?has_content></a></#if>
</#macro>


<#-- DEPRECATED, UNUSED -->
<#function asCSSClassName identifier>
  <#local cleanedUpIdentifier=identifier?replace("[!\"#$%&'\\(\\)\\*\\+,\\.\\/:;<=>\\?@\\[\\\\\\]\\^`\\{\\|}~\\s]", "", "r") />) />
  <#return cleanedUpIdentifier?replace("([A-Z][a-z])|([A-Z]$)", " $0", "r")?lower_case?replace(" ", "-") />
</#function>

<#-- DEPRECATED, UNUSED -->
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
