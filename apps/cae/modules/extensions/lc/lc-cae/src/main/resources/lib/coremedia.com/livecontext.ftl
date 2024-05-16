<#ftl strip_whitespace=true>
<#-- @ftlvariable name="liveContextFreemarkerFacade" type="com.coremedia.livecontext.web.taglib.LiveContextFreemarkerFacade" -->
<#-- @ftlvariable name="liveContextLoginFreemarkerFacade" type="com.coremedia.livecontext.web.taglib.LiveContextLoginFreemarkerFacade" -->

<#-- -------------------------------------------------------------------------------------------------------------------
 *
 * Please check the section "CoreMedia FreeMarker API" in chapter "Reference" in the "Frontend Developer Guide" for
 * details and examples for the following directives.
 * Any changes, additions or removals need to be documented in the manual.
 *
 ------------------------------------------------------------------------------------------------------------------- -->


<#-- FORMAT PRICE -->
<#function formatPrice amount currency locale>
  <#return liveContextFreemarkerFacade.formatPrice(amount, currency, locale)>
</#function>

<#-- PRODUCT -->
<#function createProductInSite product>
  <#return liveContextFreemarkerFacade.createProductInSite(product)/>
</#function>

<#-- PREVIEW METADATA -->
<#function previewMetaData>
  <#return liveContextFreemarkerFacade.getPreviewMetadata()>
</#function>

<#-- AUGMENTED CHECK -->
<#function augmentedContent>
  <#return liveContextFreemarkerFacade.isAugmentedContent()>
</#function>

<#-- GET VENDOR NAME -->
<#function getVendorName>
  <#return liveContextFreemarkerFacade.getVendorName()>
</#function>

<#-- GET HOME PAGE -->
<#function getHomePage>
  <#return liveContextFreemarkerFacade.getHomePage()>
</#function>

<#-- GET LOGIN STATUS URL -->
<#function getStatusUrl>
  <#return liveContextLoginFreemarkerFacade.getStatusUrl()>
</#function>

<#-- GET ABSOLUTE URL -->
<#function getLoginFormUrl>
  <#return liveContextLoginFreemarkerFacade.getLoginFormUrl()>
</#function>

<#-- GET LOGOUT URL -->
<#function getLogoutUrl>
  <#return liveContextLoginFreemarkerFacade.getLogoutUrl()>
</#function>

<#-- AVAILABILITY -->
<#macro availability product ifTrue="true" ifFalse="false" default=cm.UNDEFINED>
  <#-- @ftlvariable name="product" type="com.coremedia.livecontext.ecommerce.catalog.Product" -->
  <#-- written in a way that the compress directive does not strip whitespaces of the given variables -->
  <#if !liveContextFreemarkerFacade.isFragmentRequest()>
    <#if cm.isUndefined(default)>
      <#local default=ifTrue />
    </#if><@compress single_line=true>
    </@compress>${default}<@compress single_line=true>
  </@compress><#else>
    <#-- feature is currently only implemented for salesforce -->
    <#if ["sfra", "sfcc"]?seq_contains((liveContextFreemarkerFacade.vendorName!"")?lower_case)><@compress single_line=true>
      </@compress><!--VTL $include.availability('${product.externalId}','${ifTrue?json_string}','${ifFalse?json_string}') VTL--><@compress single_line=true>
    </@compress><#else><@compress single_line=true>
      <#-- fall back to old behavior -->
      </@compress>${liveContextFreemarkerFacade.isProductAvailable(product)?then(ifTrue, ifFalse)}<@compress single_line=true>
    </@compress></#if>
  </#if>
</#macro>

<#function createBeansFor contents>
    <#return liveContextFreemarkerFacade.createBeansFor(contents)>
</#function>

<#function createBeanFor content>
    <#return liveContextFreemarkerFacade.createBeanFor(content)>
</#function>
