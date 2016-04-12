<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#assign islast=cm.localParameters().islast!false />
<#assign istext="" />
<#assign hasVideo=self.data?has_content || self.dataUrl?has_content />
<#assign videoLink = bp.getVideoLink(self) />
<#assign svgSprite=bp.setting(cmpage, "svgSprite", cm.UNDEFINED) />
<#assign svgLink = svgSprite.data?has_content?then(cm.getLink(svgSprite.data), "") />

<#if self.teaserText?has_content>
  <#assign istext="is-text" />
</#if>

<div class="cm-square cm-square--video ${istext}<#if !(islast)> is-last</#if>"<@cm.metadata self.content />>
  <div class="cm-square__wrapper">

  <@bp.optionalLink href="${videoLink}" attr={"data-cm-popup": "", "class":"cm-square__popup-opener"}>
    <#-- picture -->
    <#if self.picture?has_content>
      <@cm.include self=self.picture params={
      "limitAspectRatios": [ "portrait_ratio1x1" ],
      "classBox": "cm-square__picture-box",
      "classImage": "cm-square__picture",
      "metadata": ["properties.pictures"]
      }/>
    <#else>
      <div class="cm-square__picture-box" <@cm.metadata "properties.pictures" />>
        <div class="cm-square__picture cm-image--blank"></div>
      </div>
    </#if>
    <div class="cm-square__dimmer"></div>

    <#-- play overlay icon-->
    <#if hasVideo>
      <div class="cm-square__play cm-play-button">
        <#if svgLink?has_content>
          <svg class="cm-play-button__svg"><use xlink:href="${svgLink}#play-button"></use></svg>
        <#else>
          <div class="cm-play-button__png"></div>
        </#if>
      </div>
    </#if>

    <div class="cm-square__caption caption">
      <#-- teaser title -->
      <#if self.teaserTitle?has_content>
        <h3 class="cm-square__headline" <@cm.metadata "properties.teaserTitle" />>
          <span>${self.teaserTitle!""}</span>
        </h3>
      </#if>
      <#-- teaser text -->
      <#if self.teaserText?has_content>
        <p class="cm-square__text" <@cm.metadata "properties.teaserText" />>
          <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "square.max.length", 115)) />
        </p>
      </#if>
    </div>
  </@bp.optionalLink>

  </div>
</div>
