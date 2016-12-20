<#-- @ftlvariable name="self" type="com.coremedia.blueprint.assets.contentbeans.AMAssetRendition" -->

<#if bp.isDisplayableImage(self.blob)>
  <@cm.include self=self view="asPicture" />
<#elseif bp.isDisplayableVideo(self.blob)>
  <@cm.include self=self view="asVideo" />
<#else>
  <#-- TODO: CMS-6811 this is a message for the preview which should be localized in the language of the studio user. -->
  <span<@cm.metadata data=[self.asset.content, "properties." + self.name] />>Rendition is not displayable.</span>
</#if>
