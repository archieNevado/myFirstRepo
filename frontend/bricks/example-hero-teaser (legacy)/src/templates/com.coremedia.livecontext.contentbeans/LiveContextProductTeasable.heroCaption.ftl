<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextProductTeasable" -->

<#import "*/node_modules/@coremedia/brick-utils/src/freemarkerLibs/components.ftl" as components />
<#import "*/node_modules/@coremedia/brick-utils/src/freemarkerLibs/utils.ftl" as utils />
<#import "../../freemarkerLibs/heroTeaser.ftl" as heroTeaser />

<#assign renderTeaserText=cm.localParameters().renderTeaserText!true />
<#assign heroBlockClass=cm.localParameters().heroBlockClass!"cm-hero" />

<#assign link=heroTeaser.getLink(self.productInSite!cm.UNDEFINED, self.teaserSettings) />

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
      <h4>${self.teaserTitle!""}</h4>
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
<#assign renderCTA=cm.localParameters().renderCTA!true /> <#-- de-/activate CTAs generally-->

<@heroTeaser.renderCaption title=(cm.localParameters().renderTeaserTitle!true)?then(self.teaserTitle!"", "")
                           text=textHtml?no_esc
                           link=link
                           openInNewTab=self.openInNewTab
                           ctaButtons=renderCTA?then(self.callToActionSettings, [])
                           heroBlockClass=heroBlockClass
                           metadataTitle=["properties.teaserTitle"]
                           metadataText=["properties.teaserText"] />
<#--custom call-to-action button-->
<#if self.isShopNowEnabled(cmpage.context)>
  <#-- button -->
  <div class="${heroBlockClass}__shop-now">
    <@components.button text=cm.getMessage("button_shop_now") attr={
      "classes": ["${heroBlockClass}__shop-now-button"]
    } />
  </div>
</#if>
