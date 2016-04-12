<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->
<#-- @ftlvariable name="svgSprite" type="com.coremedia.blueprint.common.contentbeans.CMImage" -->

<#assign cssClasses = cm.localParameter("cssClass", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign hasVideo=self.data?has_content || self.dataUrl?has_content />
<#assign videoLink = bp.getVideoLink(self) />
<#assign svgSprite=bp.setting(cmpage, "svgSprite", cm.UNDEFINED) />
<#assign svgLink = svgSprite.data?has_content?then(cm.getLink(svgSprite.data), "") />

<div class="cm-claim cm-claim--video thumbnail ${cssClasses}"<@cm.metadata self.content />>
  <@bp.optionalLink href="${videoLink}" attr={"data-cm-popup": "", "class":"cm-claim__popup-opener"}>
    <#-- picture -->
    <#if self.picture?has_content>
      <@cm.include self=self.picture params={
      "limitAspectRatios": [ "portrait_ratio1x1" ],
      "classBox": "cm-claim__picture-box",
      "classImage": "cm-claim__picture",
      "metadata": ["properties.pictures"]
      }/>
    <#else>
      <div class="cm-claim__picture-box">
        <div class="cm-claim__picture cm-image--blank"></div>
      </div>
    </#if>

    <#-- play overlay icon-->
    <#if hasVideo>
      <div class="cm-claim__play cm-play-button">
        <#if svgLink?has_content>
          <svg class="cm-play-button__svg"><use xlink:href="${svgLink}#play-button"></use></svg>
        <#else>
          <div class="cm-play-button__png"></div>
        </#if>
      </div>
    </#if>
  </@bp.optionalLink>

  <div class="caption">
    <#-- headline -->
    <h3 class="cm-claim__headline thumbnail-label"<@cm.metadata "properties.teaserTitle" />>
      <span>
        ${self.teaserTitle!""}
      </span>
    </h3>
    <#-- teaser text, 3 lines ~ 120 chars -->
    <p class="cm-claim__text"<@cm.metadata "properties.teaserText" />>
      <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "claim.max.length", 115)) />
    </p>
  </div>
</div>
