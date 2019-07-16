<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextExternalChannel" -->

<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign aspectRatiosSuffix=cm.localParameters().aspectRatiosSuffix!"teaser"/>
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />

<#assign pictureParams={
  "limitAspectRatios": bp.setting(cmpage.navigation, "default_aspect_ratios_for_"+aspectRatiosSuffix, []),
  "classBox": "${blockClass}__picture-box",
  "classMedia": "${blockClass}__picture",
  <#--player settings for video and audio -->
  "hideControls": true,
  "autoplay": true,
  "loop": true,
  "muted": true,
  "preload": true
} />
<#if self.firstMedia?has_content>
  <@cm.include self=self.firstMedia view="media" params=pictureParams + {"metadata": ["properties.pictures"]}/>
<#elseif self.category.catalogPicture?has_content>
  <@cm.include self=(self.category.catalogPicture)!cm.UNDEFINED view="media" params=pictureParams />
<#else>
<div class="${blockClass}__picture-box" <@preview.metadata "properties.pictures" />>
    <div class="${blockClass}__picture"></div>
</div>
</#if>
<#if renderDimmer>
  <div class="${blockClass}__dimmer"></div>
</#if>