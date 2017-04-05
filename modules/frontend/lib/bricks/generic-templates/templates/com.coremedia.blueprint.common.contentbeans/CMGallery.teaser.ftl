<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign additionalClass=cm.localParameters().additionalClass!"cm-teasable" />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderCTA=cm.localParameter("renderCTA", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

<div class="${additionalClass}  ${additionalClass}--gallery ${cssClasses}"<@cm.metadata self.content />>
    <div class="${additionalClass}__wrapper">
    <@bp.optionalLink href="${link}">
    <#-- picture -->
      <#assign picture=cm.UNDEFINED />
      <#assign metadata=[] />
      <#if self.picture?has_content>
        <#assign picture=self.picture />
        <#assign metadata=["properties.pictures"]/>
      <#elseif self.items?has_content>
        <#assign picture=self.items[0] />
        <#assign metadata=["properties.items"]/>
      </#if>
      <#if picture?has_content>
        <@cm.include self=picture params={
        "limitAspectRatios": bp.setting(cmpage.navigation, "default_aspect_ratios_for_square_teaser", []),
        "classBox": "${additionalClass}__picture-box",
        "classImage": "${additionalClass}__picture",
        "metadata": metadata
        }/>
        <#if renderDimmer>
          <div class="${additionalClass}__dimmer"></div>
        </#if>
      <#elseif renderEmptyImage>
          <div class="${additionalClass}__picture-box" <@cm.metadata "properties.pictures" />>
              <div class="${additionalClass}__picture"></div>
          </div>
      </#if>

        <div class="${additionalClass}__caption caption">
        <#-- teaser title -->
          <#if self.teaserTitle?has_content>
              <h3 class="${additionalClass}__headline" <@cm.metadata "properties.teaserTitle" />>
                  <span>${self.teaserTitle!""}</span>
              </h3>
          </#if>
          <#-- teaser text -->
          <#if renderTeaserText &&  self.teaserText?has_content>
              <p class="${additionalClass}__text" <@cm.metadata "properties.teaserText" />>
                <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "square.max.length", 115)) />
              </p>
          </#if>
          <#-- custom call-to-action button -->
          <#if renderCTA>
            <@cm.include self=self view="_callToAction" params={"additionalClass": "${additionalClass}__button cm-button--white "}/>
          </#if>
        </div>
    </@bp.optionalLink>
    </div>

  <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_TEASER />
</div>
