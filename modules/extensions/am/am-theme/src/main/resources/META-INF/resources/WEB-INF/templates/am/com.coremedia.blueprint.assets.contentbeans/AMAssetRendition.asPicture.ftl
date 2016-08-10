<#-- @ftlvariable name="self" type="com.coremedia.blueprint.assets.contentbeans.AMAssetRendition" -->

<#if self.blob?has_content>
  <img <@cm.metadata data=[self.asset.content, "properties." + self.name]/> src="${cm.getLink(self)}" style="max-width: 100%"/>
</#if>
