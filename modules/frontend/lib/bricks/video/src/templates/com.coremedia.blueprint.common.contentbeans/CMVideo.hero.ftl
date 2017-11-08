<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign additionalClass=cm.localParameters().additionalClass!"cm-hero" />
<#assign videoLink = bp.getVideoLink(self) />

<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderCTA=cm.localParameter("renderCTA", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

<div class="${additionalClass} ${additionalClass}--video"<@cm.metadata self.content />>
  <@bp.optionalLink href="${videoLink}" attr={"data-cm-popup": "", "class":"cm-claim__popup-opener"}>
    <#-- picture -->
    <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix=additionalClass displayDimmer=renderDimmer displayEmptyImage=renderEmptyImage limitAspectRatios=bp.setting(cmpage.navigation, "default_aspect_ratios_for_hero_teaser", [])/>

    <#-- play overlay icon-->
    <@cm.include self=self view="_playButton" params={"additionalClass": "${additionalClass}"}/>

    <div class="${additionalClass}__dimmer"></div>
  </@bp.optionalLink>

  <#if self.teaserTitle?has_content>
    <#-- with banderole -->
    <div class="${additionalClass}__banderole row">
      <div class="col-xs-10 col-xs-push-1">
        <#-- headline -->
        <h1 class="${additionalClass}__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
        <#-- teaser text -->
        <#if renderTeaserText && self.teaserText?has_content>
          <p class="${additionalClass}__text"<@cm.metadata "properties.teaserText" />>
            <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "hero.max.length", 140)) />
          </p>
        </#if>
      </div>
    </div>
  </#if>
</div>
