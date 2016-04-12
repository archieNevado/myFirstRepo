<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.util.List<String>" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->
<#-- @ftlvariable name="zoomBoxId" type="java.lang.String" -->
<#-- @ftlvariable name="fullImageLink" type="java.lang.String" -->
<#-- @ftlvariable name="classImage type="java.lang.String" -->
<#-- @ftlvariable name="additionalAttr type="java.lang.Object -->
<#-- get uncropped image as fullImage -->
<#if limitAspectRatios?size == 1>
  <#assign fullImageLink=bp.getBiggestImageLink(self, limitAspectRatios?last)/>
</#if>
<#if !fullImageLink?has_content>
  <#assign fullImageLink=bp.uncroppedImageLink(self) />
</#if>

<div class="cm-lightbox ${classBox}"<@cm.metadata self.content />>
  <a href="${fullImageLink}" title="${self.title!""}" data-cm-popup="gallery">
    <@cm.include self=self params={
    "limitAspectRatios": limitAspectRatios,
    "classBox": "cm-aspect-ratio-box",
    "classImage": "cm-aspect-ratio-box__content",
    "additionalAttr" : {"data-zoom-image": fullImageLink }
    }/>
  </a>
</div>
