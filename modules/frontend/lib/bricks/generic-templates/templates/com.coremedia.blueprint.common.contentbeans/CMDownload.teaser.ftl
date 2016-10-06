<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMDownload" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign additionalClass=cm.localParameters().additionalClass!"cm-teasable" />
<#assign link=cm.getLink(self!cm.UNDEFINED) />

<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderCTA=cm.localParameter("renderCTA", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

<div class="${additionalClass} ${additionalClass}--download ${cssClasses}"<@cm.metadata self.content />>
    <div class="${additionalClass}__wrapper">
    <@bp.optionalLink href="${link}">
        <#-- picture -->
        <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix=additionalClass displayEmptyImage=renderEmptyImage displayDimmer=renderDimmer limitAspectRatios=bp.setting(cmpage.navigation, "default_aspect_ratios_for_teaser", [])/>


        <div class="${additionalClass}__caption caption">
        <#-- teaser title -->
          <#if self.teaserTitle?has_content>
              <h3 class="${additionalClass}__headline" <@cm.metadata "properties.teaserTitle" />>
          <span>
            <#if link?has_content>
                <i class="glyphicon glyphicon-download" aria-hidden="true"></i>
            </#if>
          ${self.teaserTitle!""}</span>
              </h3>
          </#if>
          <#-- teaser text -->
          <#if renderTeaserText && self.teaserText?has_content>
              <p class="${additionalClass}__text" <@cm.metadata "properties.teaserText" />>
                <#if self.data?has_content>
                    <span<@cm.metadata "properties.data" />>(${cm.getLink(self)?keep_after_last(".")?keep_before("?") + ", "} ${bp.getDisplaySize(self.data.size)})</span>
                    <br/>
                </#if>
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
</div>
