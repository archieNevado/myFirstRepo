<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMDownload" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />
<#import "*/node_modules/@coremedia/brick-media/src/freemarkerLibs/media.ftl" as media />

<#assign cssClasses = cm.localParameter("cssClass", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<div class="cm-claim thumbnail ${cssClasses}"<@preview.metadata self.content />>
  <#-- picture -->
  <@utils.optionalLink href="${link}">
    <#if self.picture?has_content>
      <@cm.include self=self.picture view="media" params={
      "limitAspectRatios": [ "portrait_ratio1x1" ],
      "classBox": "cm-claim__picture-box",
      "classMedia": "cm-claim__picture",
      "metadata": ["properties.pictures"]
      }/>
    <#else>
      <div class="cm-claim__picture-box">
        <@media.renderEmptyMedia additionalClass="cm-claim__picture" />
      </div>
    </#if>
  </@utils.optionalLink>
  <div class="caption">
    <#-- headline -->
    <@utils.optionalLink href="${link}">
      <h3 class="cm-claim__headline thumbnail-label"<@preview.metadata "properties.teaserTitle" />>
        <span>
          <#if link?has_content>
              <i class="glyphicon glyphicon-download" aria-hidden="true"></i>
          </#if>
          ${self.teaserTitle!""}
        </span>
      </h3>
    </@utils.optionalLink>
    <#-- teaser text, 3 lines ~ 120 chars -->
    <p class="cm-claim__text"<@preview.metadata "properties.teaserText" />>
      <@cm.include self=self view="infos" /><br>
      <@utils.renderWithLineBreaks text=bp.truncateText(self.teaserText!"", bp.setting(self, "claim.max.length", 115)) />
    </p>
  </div>
</div>
