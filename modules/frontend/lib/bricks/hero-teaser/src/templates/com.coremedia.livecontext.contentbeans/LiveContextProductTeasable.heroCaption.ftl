<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextProductTeasable" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/components.ftl" as components />
<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />
<#import "../../freemarkerLibs/heroTeaser.ftl" as heroTeaser />

<#assign renderTeaserText=cm.localParameters().renderTeaserText!true />
<#assign heroBlockClass=cm.localParameters().heroBlockClass!"cm-teasable" />

<#assign link=(cm.localParameters().renderLink!true)?then(cm.getLink(self.productInSite!cm.UNDEFINED), "") />

<#assign textHtml>
  <#if renderTeaserText>
    <#if self.teaserText?has_content>
      <@utils.renderWithLineBreaks text=bp.truncateText(self.teaserText!"", bp.setting(self, "hero-max-length", 140)) />
    <#else>
      <@cm.include self=self.product.shortDescription/>
    </#if>
  </#if>
  <div class="${heroBlockClass}__info cm-product-info">
    <#-- headline -->
    <div class="cm-product-info__title">
      <h4 class="cm-heading4">${self.teaserTitle!""}</h4>
    </div>
    <#-- price -->
    <div class="cm-product-info__pricing">
      <@cm.include self=self.product!cm.UNDEFINED view="pricing" params={
        "classListPrice": "cm-product-info__listprice cm-price--teaser",
        "classOfferPrice": "cm-product-info__offerprice cm-price--teaser"
      } />
    </div>
  </div>
</#assign>

<@heroTeaser.renderCaption title=(cm.localParameters().renderTeaserTitle!true)?then(self.teaserTitle!"", "")
                           text=textHtml?no_esc
                           link=link
                           openInNewTab=self.openInNewTab
                           ctaButtons=self.callToActionSettings
                           heroBlockClass=heroBlockClass
                           metadataTitle=["properties.teaserTitle"]
                           metadataText=["properties.teaserText"] />
<#--custom call-to-action button-->
<#if self.isShopNowEnabled(cmpage.context)>
  <#assign quickInfoId=bp.generateId("cm-quickinfo-") />
  <#-- button -->
  <div class="cm-button-group--shopnow cm-button-group cm-button-group--overlay">
    <@components.button text=bp.getMessage("button_shop_now") attr={
      "classes": ["cm-button-group__button", "cm-button--primary", "cm-button--shadow"],
      "data-cm-button--quickinfo": '{"target": "${quickInfoId!""}"}'
    } />
  </div>
  <#-- quickinfo -->
  <@cm.include self=self view="asQuickInfo" params={
      "quickInfoId": quickInfoId!"",
      "quickInfoGroup": "product-teasers",
      "quickInfoModal": true,
      "classQuickInfo": "${heroBlockClass}__shop-now cm-quickinfo--shop-now",
      "metadata": ["properties.target"],
      "overlay": {
        "displayTitle": true,
        "displayShortText": true,
        "displayPicture": true,
        "displayDefaultPrice": true,
        "displayDiscountedPrice": true,
        "displayOutOfStockLink": true
      }
  } />
</#if>
