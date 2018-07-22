<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->

<#assign crop=cm.localParameter("crop") />

<div class="cm-image-preview">
  <#if !cm.isUndefined(crop)>
    <@cm.include self=self view="media" params={
      "limitAspectRatios": [crop],
      "metadataMedia": ["properties.data.${crop}"],
      "classBox": "cm-image-preview__picture-box",
      "classMedia": "cm-image-preview__picture"
    }/>
  </#if>
</div>
