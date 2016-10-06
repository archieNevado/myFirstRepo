<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.CMProductTeaser" -->
<#assign additionalClass=cm.localParameters().additionalClass!"cm-teasable" />
<#assign aspectRatiosSuffix=cm.localParameters().aspectRatiosSuffix!"teaser"/>

<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

<#assign pictureParams={
"limitAspectRatios": bp.setting(cmpage.navigation, "default_aspect_ratios_for_"+aspectRatiosSuffix, []),
"classBox": "${additionalClass}__picture-box",
"classImage": "${additionalClass}__picture"
} />
<#if self.picture?has_content>
  <@cm.include self=self.picture params=pictureParams + {"metadata": ["properties.pictures"]}/>
<#elseif self.product.catalogPicture?has_content>
  <@cm.include self=(self.product.catalogPicture)!cm.UNDEFINED params=pictureParams />
<#elseif renderEmptyImage>
  <div class="${additionalClass}__picture-box" <@cm.metadata "properties.pictures" />>
      <div class="${additionalClass}__picture"></div>
  </div>
</#if>
<#if renderDimmer>
  <div class="${additionalClass}__dimmer"></div>
</#if>