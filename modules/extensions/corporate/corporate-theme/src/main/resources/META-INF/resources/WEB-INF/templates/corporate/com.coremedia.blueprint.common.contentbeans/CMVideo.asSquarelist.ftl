<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#assign hasPicture=self.picture?has_content />
<#assign additionalClasses=hasPicture?then("cm-squarelist--dimmer", '') />
<#assign hasVideo=self.data?has_content || self.dataUrl?has_content />
<#assign videoLink = bp.getVideoLink(self) />
<#assign svgSprite=bp.setting(cmpage, "svgSprite", cm.UNDEFINED) />
<#assign svgLink = svgSprite.data?has_content?then(cm.getLink(svgSprite.data), "") />

<div class="cm-squarelist ${additionalClasses}"<@cm.metadata self.content />>
  <@bp.optionalLink href="${videoLink}" attr={"data-cm-popup": ""}>
    <#-- picture -->
    <#if hasPicture>
      <@cm.include self=self.picture params={
        "limitAspectRatios": [ "portrait_ratio1x1" ],
        "classBox": "cm-squarelist__picture-box",
        "classImage": "cm-squarelist__picture",
        "metadata": ["properties.pictures"]
      }/>
    <#else>
      <div class="cm-squarelist__picture-box"<@cm.metadata "properties.pictures" />>
        <div class="cm-squarelist__picture cm-image--blank"></div>
      </div>
    </#if>

    <#-- play overlay icon-->
    <#if hasVideo>
      <div class="cm-squarelist__play cm-play-button">
        <#if svgLink?has_content>
          <svg class="cm-play-button__svg"><use xlink:href="${svgLink}#play-button"></use></svg>
        <#else>
          <div class="cm-play-button__png"></div>
        </#if>
      </div>
    </#if>

    <#-- headline -->
    <#if self.teaserTitle?has_content>
      <h3 class="cm-squarelist__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h3>
    </#if>
  </@bp.optionalLink>
</div>
