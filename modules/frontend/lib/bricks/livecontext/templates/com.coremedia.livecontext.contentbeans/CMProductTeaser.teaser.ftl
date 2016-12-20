<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.CMProductTeaser" -->

<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign additionalClass=cm.localParameters().additionalClass!"cm-teasable" />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderCTA=cm.localParameter("renderCTA", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

<div class="${additionalClass} ${additionalClass}--product ${cssClasses}"<@cm.metadata self.content />>
    <div class="${additionalClass}__wrapper">
    <@bp.optionalLink href="${link}">
      <#-- picture -->
      <@cm.include self=self view="_picture" params={"additionalClass": additionalClass, "renderDimmer": renderDimmer, "renderEmptyImage": renderEmptyImage}/>

        <div class="${additionalClass}__caption caption">
          <#-- teaser title -->
          <#if self.teaserTitle?has_content>
              <h3 class="${additionalClass}__headline" <@cm.metadata "properties.teaserTitle" />>
                  <span>${self.teaserTitle!""}</span>
              </h3>
          </#if>
          <#-- teaser text -->
          <#if renderTeaserText && self.teaserText?has_content>
              <p class="${additionalClass}__text" <@cm.metadata "properties.teaserText" />>
                <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, additionalClass + "-max-length", 115)) />
              </p>
          </#if>
          <@cm.include self=self.product!cm.UNDEFINED view="info" params={
          "classBox": "${additionalClass}__info",
          "classPrice": "cm-price--teaser"
          } />
        <#-- custom call-to-action button -->
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
    </@bp.optionalLink>
    </div>
</div>

