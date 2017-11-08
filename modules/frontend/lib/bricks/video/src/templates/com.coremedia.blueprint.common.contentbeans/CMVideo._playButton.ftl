<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->

<#assign hasVideo=self.data?has_content || self.dataUrl?has_content />
<#assign additionalClass=cm.localParameters().additionalClass!"cm-teasable" />
<#if hasVideo>
  <div class="${additionalClass}__play cm-play-button"></div>
</#if>

