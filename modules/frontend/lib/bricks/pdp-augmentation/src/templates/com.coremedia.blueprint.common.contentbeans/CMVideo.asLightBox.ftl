<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.util.List" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->

<#assign ownPictureCssClass="" />

<div class="cm-lightbox ${classBox}"<@preview.metadata self.content />>
  <div class="cm-teasable cm-teasable--video" data-cm-teasable--video='{"preview": ".cm-teasable__content", "player": ".cm-teasable--video__video", "play": ".cm-play-button"}'>
    <#if self.picture?has_content>
      <#assign ownPictureCssClass="cm-hidden" />
      <@cm.include self=self.picture view="media" params={
        "classBox": "cm-teasable__content cm-product-assets__picture-box",
        "classMedia": "cm-product-assets__picture",
        "metadata": ["properties.pictures"]
      }/>
    </#if>
    <@cm.include self=self view="_playButton"/>
    <@cm.include self=self view="media" params={
      "classBox": "cm-teasable--video__video cm-product-assets__picture-box ${ownPictureCssClass}",
      "classMedia": "cm-product-assets__video"
    } />
  </div>
</div>
