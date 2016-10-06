<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextExternalChannel" -->

<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign additionalClass=cm.localParameters().additionalClass!"cm-teasable" />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderCTA=cm.localParameter("renderCTA", true) />

<div class="${additionalClass} ${additionalClass}--livecontext-external-channel ${cssClasses}"<@cm.metadata self.content />>
    <div class="${additionalClass}__wrapper">
    <@bp.optionalLink href="${link}">
    <#-- picture -->
      <@cm.include self=self view="_picture"/>

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
