<#-- @ftlvariable name="self" type="com.coremedia.livecontext.commercebeans.ProductInSite" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<#if self.product?has_content && (cmpage.navigation.rootNavigation)?has_content>
  <#assign cssClasses = cm.localParameter("islast", false)?then(" is-last", "") />
  <#assign additionalClass=cm.localParameters().additionalClass!"cm-teasable" />
  <#assign link=cm.UNDEFINED />

  <#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
  <#assign renderCTA=cm.localParameter("renderCTA", true) />
  <#assign renderDimmer=cm.localParameter("renderDimmer", true) />
  <#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

  <div class="${additionalClass} ${additionalClass}--product ${cssClasses}">
    <div class="${additionalClass}__wrapper">
    <@bp.optionalLink href="${link}">
      <#-- picture -->
      <@bp.responsiveImage self=(self.product.catalogPicture)!cm.UNDEFINED classPrefix=additionalClass displayEmptyImage=renderEmptyImage displayDimmer=renderDimmer limitAspectRatios=bp.setting(cmpage.navigation, "default_aspect_ratios_for_teaser", [])/>
      <div class="${additionalClass}__caption caption">
        <@cm.include self=self.product!cm.UNDEFINED view="info" params={
        "classBox": "${additionalClass}__info",
        "classPrice": "cm-price--teaser"
        } />
      </div>
    </@bp.optionalLink>
    </div>
  </div>
</#if>