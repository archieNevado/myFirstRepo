<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextProductTeasable" -->

<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign additionalClass=cm.localParameters().additionalClass!"cm-teasable" />
<#assign link=cm.getLink(self.productInSite!cm.UNDEFINED) />
<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderCTA=cm.localParameter("renderCTA", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

<div class="${additionalClass} ${additionalClass}--product ${cssClasses}"<@cm.metadata self.content />>
  <div class="${additionalClass}__wrapper">
  <@bp.optionalLink href="${link}">
  <#-- picture -->
    <div class="picture-wrapper">
      <@cm.include self=self view="_picture" params={"additionalClass": additionalClass, "renderDimmer": renderDimmer, "renderEmptyImage": renderEmptyImage}/>
    <#--custom call-to-action button-->
      <#if self.isShopNowEnabled(cmpage.context)>
      <#-- button -->
        <div class="cm-button-group--shopnow cm-button-group cm-button-group--overlay">
          <#if (self.product?has_content && self.product.isAvailable())>
            <#-- add-to-cart button -->
            <@lc.addToCartButton product=self.product!cm.UNDEFINED withLink=cm.getLink(self.productInSite!(cm.UNDEFINED)) enableShopNow=self.isShopNowEnabled(cmpage.context) attr={"classes": ["cm-button-group__button", "cm-button--linked-large", (lc.getVendorName() == 'SAP Hybris')?then('btn btn-default', '')]} />
          </#if>
        </div>
      </#if>
    </div>
    <div class="${additionalClass}__caption">
    <#-- teaser title -->
      <#if self.teaserTitle?has_content>
        <h3 class="${additionalClass}__headline" <@cm.metadata "properties.teaserTitle" />>
          <span>${self.teaserTitle!""}</span>
        </h3>
      </#if>
    <#-- teaser text or short description -->
      <#if renderTeaserText>
        <#if self.teaserText?has_content>
          <#-- teaser text -->
          <p class="${additionalClass}__text" <@cm.metadata "properties.teaserText" />>
            <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, additionalClass + "-max-length", 115)) />
          </p>
        <#elseif self.product?has_content>
          <#-- if no teaser text exists, render the product's short description-->
          <p class="${additionalClass}__text"><@cm.include self=self.product.shortDescription/></p>
        </#if>
      </#if>
      <@cm.include self=self.product!cm.UNDEFINED view="info" params={
      "classBox": "${additionalClass}__info",
      "classPrice": "cm-price--teaser"
      } />

    </div>
  </@bp.optionalLink>
  </div>

<@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_TEASER />
</div>

