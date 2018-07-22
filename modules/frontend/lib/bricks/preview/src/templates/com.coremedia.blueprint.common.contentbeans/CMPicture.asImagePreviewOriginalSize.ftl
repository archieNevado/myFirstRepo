<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->

<div class="cm-image-preview"<@preview.metadata self.content/>>
<#-- image -->
  <@cm.include self=self view="media" params={
    "classBox": "cm-image-preview__picture-box"
  }/>

  <div class="cm-image-preview__infos">
  <#-- copyright -->
    <#if self.copyright?has_content>
      <div<@preview.metadata "properties.copyright"/> class="cm-image-preview__copyright">${self.copyright}</div>
    </#if>
  <#-- description -->
    <#if self.detailText?has_content>
      <div<@preview.metadata "properties.detailText"/> class="cm-image-preview__description"><@cm.include self=self.detailText /></div>
    </#if>
  </div>
</div>
