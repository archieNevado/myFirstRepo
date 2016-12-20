<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.CMProductTeaser" -->

<#assign additionalClass=cm.localParameters().additionalClass!"cm-hero" />

<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderCTA=cm.localParameter("renderCTA", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

<#if self.content.externalId?has_content>
  <#assign link=cm.getLink(self.target!cm.UNDEFINED) />

  <div class="${additionalClass} ${additionalClass}--product"<@cm.metadata self.content />>
    <#-- picture -->
    <@cm.include self=self view="_picture" params={"additionalClass": additionalClass, "aspectRatiosSuffix": "hero_teaser"}/>

  <#if self.teaserTitle?has_content>
  <#-- with banderole -->
      <div class="${additionalClass}__banderole row">
          <div class="col-xs-10 col-xs-push-1">
          <#-- headline -->
            <@bp.optionalLink href="${link}">
                <h1 class="${additionalClass}__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
            </@bp.optionalLink>
            <#-- teaser text -->
            <#if renderTeaserText && self.teaserText?has_content>
                <p class="${additionalClass}__text"<@cm.metadata "properties.teaserText" />>
                  <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, additionalClass+"-max-length", 140)) />
                </p>
            </#if>
            <@cm.include self=self.product!cm.UNDEFINED view="info" params={
            "classBox": "${additionalClass}__info",
            "classPrice": "cm-price--teaser"
          } />
          <#-- custom call-to-action button -->
          <#-- shop now button and quickinfo -->
          <#if self.isShopNowEnabled(cmpage.context)>
            <#assign quickInfoId=bp.generateId("cm-quickinfo-") />
            <#-- button -->
                <div class="cm-button-group--shopnow cm-button-group cm-button-group--overlay">
                  <@bp.button text=bp.getMessage("button_shop_now") attr={
                  "classes": ["cm-button-group__button", "cm-button--primary", "cm-button--shadow"],
                  "data-cm-button--quickinfo": '{"target": "${quickInfoId!""}"}'
                  } />
                </div>
            <#-- quickinfo -->
              <@cm.include self=self view="asQuickInfo" params={
              "quickInfoId": quickInfoId!"",
              "quickInfoGroup": "product-teasers",
              "quickInfoModal": true,
              "classQuickInfo": "cm-teasable__quickinfo",
              "metadata": ["properties.target"],
              "overlay": {
              "displayTitle": true,
              "displayShortText": true,
              "displayPicture": true,
              "displayDefaultPrice": true,
              "displayDiscountedPrice": true,
              "displayOutOfStockLink": true
              }
              }
              />
            </#if>
          </div>
      </div>
  </#if>
</div>
</#if>
