<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMDownload" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />
<#import "*/node_modules/@coremedia/brick-media/src/freemarkerLibs/media.ftl" as media />

<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<div class="cm-medialist"<@preview.metadata self.content />>
  <@utils.optionalLink href="${link}">
    <#-- picture -->
    <#if self.picture?has_content>
      <@cm.include self=self.picture view="media" params={
        "limitAspectRatios": [ "portrait_ratio1x1" ],
        "classBox": "cm-medialist__picture-box",
        "classMedia": "cm-medialist__picture",
        "metadata": ["properties.pictures"]
      }/>
    <#else>
      <div class="cm-medialist__picture-box">
        <@media.renderEmptyMedia additionalClass="cm-medialist__picture" />
      </div>
    </#if>
    <#-- caption -->
    <div class="cm-medialist__caption">
      <#-- date -->
      <@utils.renderDate date=self.externallyDisplayedDate.time
                         cssClass="cm-medialist__time"
                         metadata=["properties.externallyDisplayedDate"] />
      <#-- headline -->
      <#if self.teaserTitle?has_content>
        <h3 class="cm-medialist__headline"<@preview.metadata "properties.teaserTitle" />>
          <#if link?has_content>
            <i class="glyphicon glyphicon-download" aria-hidden="true"></i>
          </#if>
          ${self.teaserTitle!""}
        </h3>
      </#if>
      <#-- teaser text, 3 lines ~ 160 chars -->
      <p class="cm-medialist__text"<@preview.metadata "properties.teaserText" />>
        <@cm.include self=self view="infos" /><br>
        <@utils.renderWithLineBreaks text=bp.truncateText(self.teaserText!"", bp.setting(self, "medialist.max.length", 160)) />
      </p>
    </div>
  </@utils.optionalLink>
</div>
