<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign additionalClass=cm.localParameters().additionalClass!"cm-hero" />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderCTA=cm.localParameter("renderCTA", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

<div class="${additionalClass}"<@cm.metadata self.content />>
  <@bp.optionalLink href="${link}">
    <#-- picture -->
    <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix=additionalClass displayDimmer=renderDimmer displayEmptyImage=renderEmptyImage limitAspectRatios=bp.setting(cmpage.navigation, "default_aspect_ratios_for_hero_teaser", [])/>
    <#if !self.teaserOverlaySettings.enabled>
      <#-- with banderole -->
      <div class="${additionalClass}__banderole row">
        <div class="col-xs-10 col-xs-push-1">
          <#-- headline -->
          <#if self.teaserTitle?has_content>
            <h1 class="${additionalClass}__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
          </#if>
          <#-- teaser text -->
          <#if renderTeaserText && self.teaserText?has_content>
            <p class="${additionalClass}__text"<@cm.metadata "properties.teaserText" />>
              <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, additionalClass+"-max-length", 140)) />
            </p>
          </#if>
          <#-- custom call-to-action button -->
          <#if renderCTA>
            <@cm.include self=self view="_callToAction" params={"additionalClass": "${additionalClass}__button cm-button--white "}/>
          </#if>
        </div>
      </div>
    <#else>
      <@cm.include self=self view="_teaserOverlay" />
    </#if>
  </@bp.optionalLink>
</div>
