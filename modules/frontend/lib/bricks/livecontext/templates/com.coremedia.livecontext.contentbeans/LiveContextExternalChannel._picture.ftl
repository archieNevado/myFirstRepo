<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextExternalChannel" -->
<#assign additionalClass=cm.localParameters().additionalClass!"cm-teasable" />
<#assign aspectRatiosSuffix=cm.localParameters().aspectRatiosSuffix!"teaser"/>

<#assign pictureParams={
"limitAspectRatios": bp.setting(cmpage.navigation, "default_aspect_ratios_for_"+aspectRatiosSuffix, []),
"classBox": "${additionalClass}__picture-box",
"classImage": "${additionalClass}__picture"
} />
<#if self.picture?has_content>
  <@cm.include self=self.picture params=pictureParams + {"metadata": ["properties.pictures"]}/>
<#elseif self.category.catalogPicture?has_content>
  <@cm.include self=(self.category.catalogPicture)!cm.UNDEFINED params=pictureParams />
<#else>
<div class="${additionalClass}__picture-box" <@cm.metadata "properties.pictures" />>
    <div class="${additionalClass}__picture"></div>
</div>
</#if>
<div class="${additionalClass}__dimmer"></div>