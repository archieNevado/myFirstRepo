<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/cta.ftl" as cta />
<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<#assign cssClasses = cm.localParameter("islast", false)?then(" is-last", "") />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign target=(self.target?has_content && self.target.openInNewTab)?then("_blank", "") />
<#assign rel=(self.target?has_content && self.target.openInNewTab)?then("noopener", "") />

<div class="cm-text thumbnail ${cssClasses}"<@preview.metadata self.content />>
  <#-- headline -->
  <@utils.optionalLink href="${link}" attr={"target":target,"rel":rel}>
    <h3 class="cm-text__headline"<@preview.metadata "properties.teaserTitle" />>
      <span>${self.teaserTitle!""}</span>
    </h3>
  </@utils.optionalLink>
  <#-- teaser text, 9 lines ~ 600 chars -->
  <p class="cm-text__text"<@preview.metadata "properties.teaserText" />>
    <@utils.renderWithLineBreaks text=bp.truncateText(self.teaserText!"", bp.setting(self, "text.max.length", 600)) />
  </p>
  <#-- cta -->
  <@cta.render buttons=self.callToActionSettings
               additionalClass="cm-text__cta"
               additionalButtonClass="cm-button cm-button--primary" />
</div>
