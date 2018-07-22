<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMDownload" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/cta.ftl" as cta />
<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<#assign cssClasses = cm.localParameter("islast", false)?then(" is-last", "") />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<div class="cm-text thumbnail ${cssClasses}"<@preview.metadata self.content />>
  <#-- headline -->
  <@utils.optionalLink href="${link}">
    <h3 class="cm-text__headline"<@preview.metadata "properties.teaserTitle" />>
      <#if link?has_content>
        <i class="glyphicon glyphicon-download" aria-hidden="true"></i>
      </#if>
      <span>${self.teaserTitle!""}</span>
    </h3>
  </@utils.optionalLink>
  <#-- teaser text, 9 lines ~ 600 chars -->
  <p class="cm-text__text"<@preview.metadata "properties.teaserText" />>
    <@cm.include self=self view="infos" /><br>
    <@utils.renderWithLineBreaks text=bp.truncateText(self.teaserText!"", bp.setting(self, "text.max.length", 600)) />
  </p>
  <#-- cta -->
  <@cta.render buttons=self.callToActionSettings
               additionalClass="cm-text__cta"
               additionalButtonClass="cm-button cm-button--primary" />
</div>
