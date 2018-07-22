<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />
<#import "*/node_modules/@coremedia/brick-media/src/freemarkerLibs/media.ftl" as media />

<#assign cssClasses = cm.localParameter("islast", false)?then(" is-last", "") />
<#assign hasVideo=self.data?has_content || self.dataUrl?has_content />
<#assign videoLink=media.getLink(self) />
<#assign link = cm.getLink(self.target) />

<div class="cm-text thumbnail ${cssClasses}"<@preview.metadata self.content />>
  <#-- headline -->
  <@utils.optionalLink href="${link}">
    <h3 class="cm-text__headline"<@preview.metadata "properties.teaserTitle" />>
      <span>${self.teaserTitle!""}</span>
    </h3>
  </@utils.optionalLink>
  <#-- teaser text, 9 lines ~ 600 chars -->
  <p class="cm-text__text"<@preview.metadata "properties.teaserText" />>
    <@utils.renderWithLineBreaks text=bp.truncateText(self.teaserText!"", bp.setting(self, "text.max.length", 600)) />
  </p>
  <#-- play button -->
  <#if videoLink?has_content>
    <@utils.optionalLink href=link attr={ "class": "cm-text__cta cm-button cm-button--primary btn", "data-cm-video-popup": { "url": videoLink } }>
      ${bp.getMessage("button_video")}
    </@utils.optionalLink>
  </#if>
</div>
