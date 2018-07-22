<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->

<#assign fragmentedPreviews=[]/>

<#if self.disableCropping>
  <#assign fragmentedPreviews=[
    {
      "viewName": "asImagePreviewOriginalSize",
      "titleKey": "preview_image_originalSize"
    }] />
<#else>
  <#assign fragmentViews=[
    {
      "viewName": "",
      "titleKey": "preview_label_DEFAULT"
    }] />
  <#assign fragmentedPreviews=bp.previewTypes(cmpage, self, fragmentViews)/>
  <#assign allAspectRatios=bp.setting(self, "responsiveImageSettings") />
  <#list allAspectRatios?keys as ratio>
    <#assign fragmentedPreviews=fragmentedPreviews + [
      {
        "viewName": "asImagePreviewCropped",
        "viewParams": {
          "crop": ratio
        },
        "titleKey": "preview_image_" + ratio
      }] />
  </#list>
</#if>

<#--  -->

<@cm.include self=self view="multiViewPreview" params={
  "fragmentViews": fragmentedPreviews
}/>
