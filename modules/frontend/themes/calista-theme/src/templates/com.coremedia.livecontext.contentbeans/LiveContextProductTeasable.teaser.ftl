<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextProductTeasable" -->

<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign link=cm.getLink(self.productInSite!cm.UNDEFINED) />
<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

<div class="${blockClass} ${blockClass}--product ${additionalClass} ${cssClasses}"<@preview.metadata self.content />>
  <div class="${blockClass}__wrapper">
  <#-- picture -->
    <div class="picture-wrapper">
      <@bp.optionalLink href="${link}">
        <@cm.include self=self view="_picture" params={"blockClass": blockClass, "renderDimmer": renderDimmer, "renderEmptyImage": renderEmptyImage}/>
      </@bp.optionalLink>
    <#--custom call-to-action button overlay-->
      <#if self.isShopNowEnabled(cmpage.context)>
      <#-- button -->
        <div class="cm-button-group--shopnow cm-button-group cm-button-group--overlay">
          <#if (self.product?has_content && self.product.isAvailable())>
            <#-- add-to-cart button -->
            <@lc.addToCartButton product=self.product!cm.UNDEFINED withLink=link enableShopNow=self.isShopNowEnabled(cmpage.context) attr={"classes": ["cm-button-group__button", "cm-button--linked-large", (lc.getVendorName() == 'SAP Hybris')?then('btn btn-default', '')]} />
          </#if>
        </div>
      </#if>
    </div>
    <div class="${blockClass}__caption">
    <#-- teaser title -->
      <#if self.teaserTitle?has_content>
        <@bp.optionalLink href="${link}">
          <h3 class="${blockClass}__headline" <@preview.metadata "properties.teaserTitle" />>
            <span>${self.teaserTitle!""}</span>
          </h3>
        </@bp.optionalLink>
      </#if>
    <#-- teaser text or short description -->
      <#if renderTeaserText>
        <#if self.teaserText?has_content>
          <#-- teaser text -->
          <p class="${blockClass}__text" <@preview.metadata "properties.teaserText" />>
            <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, blockClass + "-max-length", 115)) />
          </p>
        <#elseif self.product?has_content>
          <#-- if no teaser text exists, render the product's short description-->
          <p class="${blockClass}__text"><@cm.include self=self.product.shortDescription/></p>
        </#if>
      </#if>
      <@cm.include self=self.product!cm.UNDEFINED view="info" params={
      "classBox": "${blockClass}__info",
      "classPrice": "cm-price--teaser"
      } />
    </div>
  </div>
<@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_TEASER />
</div>
