<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#assign index=cm.localParameters().index!0 />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign hasVideo=self.data?has_content || self.dataUrl?has_content />
<#assign videoLink = bp.getVideoLink(self) />
<#assign svgSprite=bp.setting(cmpage, "svgSprite", cm.UNDEFINED) />
<#assign svgLink = svgSprite.data?has_content?then(cm.getLink(svgSprite.data), "") />

<div class="cm-hero cm-hero--video ${additionalClass!""}"<@cm.metadata self.content />>
  <@bp.optionalLink href="${videoLink}" attr={"data-cm-popup": "", "class":"cm-claim__popup-opener"}>
    <#-- picture -->
    <#if self.picture?has_content>
      <@cm.include self=self.picture params={
        "limitAspectRatios": [ "portrait_ratio1x1", "landscape_ratio16x9" ],
        "classBox": "cm-hero__picture-box",
        "classImage": "cm-hero__picture",
        "metadata": ["properties.pictures"]
      }/>
    <#else>
      <div class="cm-hero__picture-box" <@cm.metadata "properties.pictures" />>
        <div class="cm-hero__picture"></div>
      </div>
    </#if>

    <#-- play overlay icon-->
    <#if hasVideo>
      <div class="cm-hero__play cm-play-button">
        <#if svgLink?has_content>
          <svg class="cm-play-button__svg"><use xlink:href="${svgLink}#play-button"></use></svg>
        <#else>
          <div class="cm-play-button__png"></div>
        </#if>
      </div>
    </#if>

    <div class="cm-hero__dimmer"></div>
  </@bp.optionalLink>

  <#if self.teaserTitle?has_content>
    <#-- with banderole -->
    <div class="cm-hero__banderole row">
      <div class="col-xs-10 col-xs-push-1">
        <#-- headline -->
        <h1 class="cm-hero__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
        <#-- teaser text -->
        <p class="cm-hero__text"<@cm.metadata "properties.teaserText" />>
          <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "hero.max.length", 140)) />
        </p>
      </div>
    </div>
  </#if>
</div>
