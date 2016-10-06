<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="svgSprite" type="com.coremedia.blueprint.common.contentbeans.CMImage" -->

<#assign hasVideo=self.data?has_content || self.dataUrl?has_content />
<#assign svgSprite=bp.setting(cmpage, "svgSprite", cm.UNDEFINED) />
<#assign svgLink = svgSprite.data?has_content?then(cm.getLink(svgSprite.data), "") />

<#assign additionalClass=cm.localParameters().additionalClass!"cm-teasable" />

<#if hasVideo>
<div class="${additionalClass}__play cm-play-button">
  <#if svgLink?has_content>
      <svg class="cm-play-button__svg">
          <use xlink:href="${svgLink}#play-button"></use>
      </svg>
  <#else>
      <div class="cm-play-button__png"></div>
  </#if>
</div>
</#if>

