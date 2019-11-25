<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="blockClass" type="java.lang.String" -->
<#-- @ftlvariable name="cssClasses" type="java.lang.String" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->
<#-- @ftlvariable name="renderTeaserText" type="java.lang.Boolean" -->
<#-- @ftlvariable name="timelineEntries" type="java.util.List" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->
<#-- @ftlvariable name="overlay" type="java.util.Map" -->
<#-- @ftlvariable name="entry.link" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="entry.startTimeMillis" type="java.lang.Integer" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />

<div class="cm-shoppable cm-container">
<#-- video on the left -->
  <div class="${blockClass} ${cssClasses} ${blockClass}--video cm-shoppable__video ${additionalClass}" data-cm-teasable--video='{"preview": ".cm-teaser__content", "player": ".${blockClass}--video__video", "play": ".cm-play-button"}'<@preview.metadata (metadata![]) + [self.content] />>
    <div class="${blockClass}__wrapper">
      <#assign ownPictureCssClass="" />
      <#if self.picture?has_content>
        <#assign ownPictureCssClass="cm-hidden" />
        <@cm.include self=self.picture view="media" params={
        "limitAspectRatios": [ "portrait_ratio1x1", "landscape_ratio16x9" ],
        "classBox": "cm-teaser__content cm-shoppable__content",
        "metadata": ["properties.pictures"]
        }/>
      </#if>

    <#-- play overlay icon-->
      <@cm.include self=self view="_playButton" params={"blockClass": "${blockClass}"}/>

      <div class="${blockClass}__caption">
      <#-- teaser title -->
        <#if self.teaserTitle?has_content>
          <h3 class="${blockClass}__headline" <@preview.metadata "properties.teaserTitle" />>
            <span>${self.teaserTitle!""}</span>
          </h3>
        </#if>
      <#-- teaser text -->
        <#if renderTeaserText && self.teaserText?has_content>
          <p class="${blockClass}__text" <@preview.metadata "properties.teaserText" />>
            <@utils.renderWithLineBreaks text=bp.truncateText(self.teaserText!"", bp.setting(self, "teaser.max.length", 200)) />
          </p>
        </#if>
      </div>

      <@cm.include self=self view="media" params={
        "classBox": "${blockClass}--video__video cm-shoppable__player ${ownPictureCssClass}",
        "classMedia": "cm-shoppable__video-element",
        "hideControls": false,
        "preload": true} />
    </div>
  </div>
<#-- teaser on the right -->
  <div class="cm-shoppable__teasers"<@preview.metadata "properties.timeLine" />>
  <#-- default teaser -->
    <#if self.timeLineDefaultTarget?has_content>
      <div class="cm-shoppable__teaser cm-shoppable__default">
        <@cm.include self=self.timeLineDefaultTarget!cm.UNDEFINED view="asQuickInfo" params={
        "classQuickInfo":"cm-quickinfo--shoppable",
        "overlay": overlay
        }/>
      </div>
    </#if>
  <#-- list all timeline teaser -->
    <#if (timelineEntries?size > 0)>
      <#list timelineEntries as entry>
        <#if entry.startTimeMillis?has_content && entry.link?has_content>
          <div class="cm-shoppable__teaser" data-cm-video-shoppable-time="${entry.startTimeMillis}">
            <@cm.include self=entry.link view="asQuickInfo" params={
            "classQuickInfo":"cm-quickinfo--shoppable",
            "overlay": overlay
            }/>
          </div>
        </#if>
      </#list>
    </#if>
  </div>
</div>
