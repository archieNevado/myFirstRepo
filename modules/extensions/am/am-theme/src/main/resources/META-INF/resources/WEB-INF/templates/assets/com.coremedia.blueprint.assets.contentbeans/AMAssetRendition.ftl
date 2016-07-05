<#-- @ftlvariable name="self" type="com.coremedia.blueprint.assets.contentbeans.AMAssetRendition" -->

<#if bp.isDisplayableImage(self.blob)>
  <@cm.include self=self view="asPicture" />
<#elseif bp.isDisplayableVideo(self.blob)>
  <@cm.include self=self view="asVideo" />
<#else>
  <span<@cm.metadata data=[self.asset.content, "properties." + self.name] />>Rendition is not displayable.</span>
</#if>
