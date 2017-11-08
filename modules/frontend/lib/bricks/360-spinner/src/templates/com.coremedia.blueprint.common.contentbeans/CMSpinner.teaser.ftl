<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMSpinner" -->

<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign additionalClass=cm.localParameters().additionalClass!"cm-teasable" />

<#assign renderType=cm.localParameter("renderType", "spinner") />
<#assign renderTeaserTitle=cm.localParameter("renderTeaserTitle", true) />
<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderCTA=cm.localParameter("renderCTA", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />
<#assign limitAspectRatiosKey="default_aspect_ratios_for_" + cm.localParameter("limitAspectRatiosKey", "teaser") />
<#assign limitAspectRatios=cm.localParameter("limitAspectRatios", bp.setting(cmpage.navigation, limitAspectRatiosKey, [])) />

<div class="${additionalClass} ${additionalClass}--spinner ${cssClasses}"<@cm.metadata self.content />>
  <div class="${additionalClass}__wrapper">
    <#-- spinner (with at least 2 images) -->
    <#if (self.sequence![])?size gt 2 && renderType != "plain">
        <@cm.include self=self view="_spinner"/>
    <#else>
      <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix=additionalClass displayEmptyImage=renderEmptyImage displayDimmer=renderDimmer limitAspectRatios=limitAspectRatios/>
      <div class="cm-spinner__icon"></div>
    </#if>
    <#if renderTeaserTitle || renderTeaserText || renderCTA>
      <div class="${additionalClass}__caption">
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
    </#if>
  </div>

 <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_TEASER />
</div>
