<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<div class="cm-medialist cm-medialist--video"<@preview.metadata self.content />>
  <@utils.optionalLink href="${cm.getLink(self.target)}">
    <div class="cm-medialist__wrapper">
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
          <div class="cm-medialist__picture cm-image--blank"></div>
        </div>
      </#if>
      <#-- play overlay icon-->
      <@cm.include self=self view="_playButton" params={"blockClass": "cm-medialist", "openAsPopup": true}/>
    </div>

    <#-- caption -->
    <div class="cm-medialist__caption">
      <#-- date -->
      <@utils.renderDate date=self.externallyDisplayedDate.time
                         cssClass="cm-medialist__time"
                         metadata=["properties.externallyDisplayedDate"] />
      <#-- headline -->
      <#if self.teaserTitle?has_content>
        <h3 class="cm-medialist__headline"<@preview.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h3>
      </#if>
      <#-- teaser text, 3 lines ~ 160 chars -->
      <p class="cm-medialist__text"<@preview.metadata "properties.teaserText" />>
        <@utils.renderWithLineBreaks text=bp.truncateText(self.teaserText!"", bp.setting(self, "medialist.max.length", 160)) />
      </p>
    </div>
  </@utils.optionalLink>
</div>
