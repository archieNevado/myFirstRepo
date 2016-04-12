<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#assign hasVideo=self.data?has_content || self.dataUrl?has_content />
<#assign videoLink = bp.getVideoLink(self) />
<#assign svgSprite=bp.setting(cmpage, "svgSprite", cm.UNDEFINED) />
<#assign svgLink = svgSprite.data?has_content?then(cm.getLink(svgSprite.data), "") />

<div class="cm-medialist cm-medialist--video"<@cm.metadata self.content />>
  <@bp.optionalLink href="${videoLink}" attr={"data-cm-popup": ""}>
    <#-- picture -->
    <#if self.picture?has_content>
      <#if hasVideo> <!-- include a wrapper for positioning if image and video are present -->
        <div class="cm-medialist__wrapper">
      </#if>
      <@cm.include self=self.picture params={
      "limitAspectRatios": [ "portrait_ratio1x1" ],
      "classBox": "cm-medialist__picture-box",
      "classImage": "cm-medialist__picture",
      "metadata": ["properties.pictures"]
      }/>
    <#else>
      <div class="cm-medialist__picture-box">
        <div class="cm-medialist__picture cm-image--blank"></div>
      </div>
    </#if>

    <#-- play overlay icon-->
    <#if hasVideo>
      <div class="cm-medialist__play cm-play-button">
        <#if svgLink?has_content>
          <svg class="cm-play-button__svg"><use xlink:href="${svgLink}#play-button"></use></svg>
        <#else>
          <div class="cm-play-button__png"></div>
        </#if>
      </div>
    </div>
    </#if>


    <#-- caption -->
    <div class="cm-medialist__caption">
      <#-- date -->
      <#if self.externallyDisplayedDate?has_content>
       <@bp.renderDate self.externallyDisplayedDate.time "cm-medialist__time" />
      </#if>
      <#-- headline -->
      <#if self.teaserTitle?has_content>
        <h3 class="cm-medialist__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h3>
      </#if>
      <#-- teaser text, 3 lines ~ 160 chars -->
      <p class="cm-medialist__text"<@cm.metadata "properties.teaserText" />>
        <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "medialist.max.length", 160)) />
      </p>
    </div>
  </@bp.optionalLink>
</div>
