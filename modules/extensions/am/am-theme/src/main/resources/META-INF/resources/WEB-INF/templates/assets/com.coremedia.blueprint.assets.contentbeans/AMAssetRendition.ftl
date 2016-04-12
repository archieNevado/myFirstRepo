<#-- @ftlvariable name="self" type="com.coremedia.blueprint.assets.contentbeans.AMAssetRendition" -->

<#if self.blob?has_content>
  <#if bp.isDisplayableImage(self.blob)>
    <@cm.include self=self view="asPicture" />
  <#elseif bp.isDisplayableVideo(self.blob)>
    <@cm.include self=self view="asVideo" />
  <#else>
    <span<@cm.metadata data=[self.asset.content, "properties." + self.name] />>Rendition is not displayable.</span>
  </#if>
<#else>
  <span<@cm.metadata data=[self.asset.content, "properties." + self.name] />>Rendition has no content attached.</span>
</#if>
