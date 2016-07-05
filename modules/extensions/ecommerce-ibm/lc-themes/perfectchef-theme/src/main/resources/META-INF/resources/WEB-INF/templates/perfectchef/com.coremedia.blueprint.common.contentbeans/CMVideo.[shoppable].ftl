<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="entry.link" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="entry.startTimeMillis" type="java.lang.Integer" -->

<#assign timelineEntries=self.timeLineSequences![] />
<#assign overlay={
  "displayTitle": true,
  "displayShortText": true,
  "displayPicture": true,
  "displayDefaultPrice": true,
  "displayDiscountedPrice": true,
  "displayOutOfStockLink": true
} />

<#if (timelineEntries?size > 0 || self.timeLineDefaultTarget?has_content)>
  <div class="cm-shoppable cm-clearfix" data-cm-video-shoppable="true">

  <#-- video on the left -->
    <div class="cm-teaser  cm-teaser--video cm-shoppable__video cm-box" data-cm-teaser--video='{"preview": ".cm-teaser__content", "player": ".cm-teaser--video__video", "play": ".cm-teaser--video__play"}'<@cm.metadata (metadata![]) + [self.content] />>
      <#if self.picture?has_content>
        <#assign ownPictureCssClass="cm-hidden" />
        <@cm.include self=self.picture params={
        "limitAspectRatios": [ "portrait_ratio1x1", "landscape_ratio16x9" ],
        "classBox": "cm-teaser__content cm-shoppable__content cm-aspect-ratio-box",
        "classImage": "cm-aspect-ratio-box__content",
        "metadata": ["properties.pictures"]
        }/>
      </#if>
      <#if self.teaserTitle?has_content>
        <h2 class="cm-teaser__title cm-heading2 cm-heading2--boxed"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h2>
      </#if>
      <div class="cm-teaser--video__play"></div>
      <div class="cm-teaser--video__video cm-shoppable__player cm-aspect-ratio-box ${ownPictureCssClass}">
        <@cm.include self=self view="video" params={"classVideo": "cm-aspect-ratio-box__content", "hideControls": false, "shoppableVideo": true} />
      </div>
    </div>


    <#-- teaser on the right -->
    <div class="cm-shoppable__teasers cm-box"<@cm.metadata "properties.timeLine" />>
      <#-- default teaser -->
      <#if self.timeLineDefaultTarget?has_content>
        <div class="cm-shoppable__teaser cm-shoppable__default">
          <@cm.include self=self.timeLineDefaultTarget!cm.UNDEFINED view="asQuickInfo" params={
          "classQuickInfo":"",
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
                "classQuickInfo":"",
                "overlay": overlay
              }/>
            </div>
          </#if>
        </#list>
      </#if>
    </div>
  </div>
<#else>
  <@cm.include self=self view="[]" />
  <@bp.notification type="error" text="This is a shoppable video without timeline entries." />
</#if>
