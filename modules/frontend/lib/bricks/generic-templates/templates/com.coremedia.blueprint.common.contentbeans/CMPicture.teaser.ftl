<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#-- same as CMTeasable.teaser.ftl but without a link. -->
<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign additionalClass=cm.localParameters().additionalClass!"cm-teasable" />

<#assign renderLink=cm.localParameter("renderLink", true) />
<#assign renderTeaserTitle=cm.localParameter("renderTeaserTitle", true) />
<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />
<#assign limitAspectRatiosKey="default_aspect_ratios_for_" + cm.localParameter("limitAspectRatiosKey", "teaser") />
<#assign limitAspectRatios=cm.localParameter("limitAspectRatios", bp.setting(cmpage.navigation, limitAspectRatiosKey, [])) />

<div class="${additionalClass} ${cssClasses}"<@cm.metadata self.content />>
    <div class="${additionalClass}__wrapper">
      <#-- picture -->
      <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix=additionalClass displayEmptyImage=renderEmptyImage displayDimmer=renderDimmer limitAspectRatios=limitAspectRatios/>
      <#if renderTeaserTitle || renderTeaserText || renderCTA>
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
        </div>
      </#if>
    </div>

  <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_TEASER />
</div>
