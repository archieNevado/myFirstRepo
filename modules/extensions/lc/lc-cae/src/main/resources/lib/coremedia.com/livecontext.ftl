<#ftl strip_whitespace=true>
<#-- @ftlvariable name="liveContextFreemarkerFacade" type="com.coremedia.livecontext.web.taglib.LiveContextFreemarkerFacade" -->
<#-- @ftlvariable name="liveContextLoginFreemarkerFacade" type="com.coremedia.livecontext.web.taglib.LiveContextLoginFreemarkerFacade" -->
<#-- @ftlvariable name="blueprintFreemarkerFacade" type="com.coremedia.blueprint.cae.web.taglib.BlueprintFreemarkerFacade" -->

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


<#-- --- DEPRECATED, UNUSED ---------------------------------------------------------------------------------------- -->

<#-- DEPRECATED, see Frontend Developer Guide -->
<#macro addToCartButton product alwaysShow=false alwaysClickable=false enableShopNow=true withLink="" attr={}>
  <#local numberOfVariants=(product.variants?size)!0 />
  <#local hasSingleSKU=(numberOfVariants == 1) />
  <#local isProductAvailable=(product.isAvailable())!false />

  <#-- variant 1) unavailable -->
  <#local buttonLabel=bp.getMessage("cart_unavailable") />
  <#local buttonData={} />
  <#local buttonClasses=[] />
  <#local iconClass="" />

  <#-- variant 2) available -->
  <#if (alwaysShow || isProductAvailable)>
    <#local buttonLabel=bp.getMessage("cart_view_variants") />
    <#local buttonClasses=buttonClasses + ["cm-button--primary"] />

  <#-- variant 3) available with one sku -->
    <#if (enableShopNow && (alwaysClickable || hasSingleSKU))>
      <#local cart=cm.substitute("cart", product) />
      <#local buttonLabel=bp.getMessage("cart_add_item") />
      <#local externalTechId=hasSingleSKU?then(product.variants[0].externalTechId, product.externalTechId)/>
      <#local buttonData={"data-cm-cart-add-item": '{"id": "${externalTechId!""}", "link": "${cm.getLink(cart, "ajax")}", "cart": ".cm-cart" }'} />
      <#local iconClass="icon-none" />
    </#if>
  </#if>

  <#local attr=bp._extendSequenceInMap(attr, "classes", buttonClasses) />

  <#local link="" />
  <#if (withLink?has_content && ((!alwaysClickable && !hasSingleSKU) || !enableShopNow))>
    <#local link=withLink>
  </#if>
  <@bp.button text=buttonLabel href=link baseClass="cm-button" iconClass=iconClass attr=(attr + buttonData) />
</#macro>

<#-- DEPRECATED, use bp.getPlacementHighlightingMetaData instead -->
<#function fragmentHighlightingMetaData placement>
  <#return blueprintFreemarkerFacade.getPlacementHighlightingMetaData(placement)>
</#function>

<#-- DEPRECATED, UNUSED -->
<#function fragmentContext>
  <#return liveContextFreemarkerFacade.fragmentContext()>
</#function>

<#-- DEPRECATED -->
<#function getSecureScheme>
  <#return liveContextFreemarkerFacade.getSecureScheme() />
</#function>

<#-- DEPRECATED, UNUSED -->
<#macro getLoginLink linkClass="">
  <#local cssClass=""/>
  <#if linkClass?has_content>
    <#local cssClass="class=\"" + linkClass + "\""/>
  </#if>
  <a id="cm-login" ${cssClass} data-loginstatus="${liveContextLoginFreemarkerFacade.getStatusUrl()}" href="${liveContextLoginFreemarkerFacade.getLoginFormUrl()}" style="display: none" title="Login">Login</a>
  <a id="cm-logout" ${cssClass} data-loginstatus="${liveContextLoginFreemarkerFacade.getStatusUrl()}" href="${liveContextLoginFreemarkerFacade.getLogoutUrl()}" style="display: none" title="Logout">Logout</a>
</#macro>
