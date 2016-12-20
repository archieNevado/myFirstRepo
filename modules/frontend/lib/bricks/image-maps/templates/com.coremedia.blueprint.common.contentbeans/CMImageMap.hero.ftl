<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMImageMap" -->

<#assign additionalClass=cm.localParameters().additionalClass!"cm-hero" />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderCTA=cm.localParameter("renderCTA", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />
<#assign imageMapParams=bp.initializeImageMap()/>
<#assign useQuickinfo=cm.localParameter("imagemap_use_quickinfo", true)/>

<div class="${additionalClass} ${additionalClass}--imagemap cm-imagemap"<@cm.metadata self.content />
     data-cm-imagemap='{"coordsBaseWidth": "${bp.IMAGE_TRANSFORMATION_BASE_WIDTH}", "defaultLink": "${cm.getLink(self.target!cm.UNDEFINED)}"}'>

  <#-- picture -->
  <@cm.include self=self view="_picture" params={
    "additionalClass": additionalClass,
    "renderDimmer": renderDimmer,
    "renderEmptyImage": renderEmptyImage,
    "limitAspectRatios": bp.setting(cmpage.navigation, "default_aspect_ratios_for_hero_teaser", []),
    "useQuickinfo": useQuickinfo} +
    imageMapParams
  />

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
            <#-- custom call-to-action button -->
            <#if renderCTA>
              <@cm.include self=self view="_callToAction" params={"additionalClass": "${additionalClass}__button cm-button--white "}/>
            </#if>
          </div>
      </div>
  </#if>

  <#--include imagemap quick icons-->
  <#if useQuickinfo>
    <@cm.include self=self view="_areasQuickInfo" params=imageMapParams/>
  </#if>
</div>
