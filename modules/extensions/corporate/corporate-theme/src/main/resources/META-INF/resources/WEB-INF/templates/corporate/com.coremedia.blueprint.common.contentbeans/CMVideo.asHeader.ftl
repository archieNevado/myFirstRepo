<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->

<#assign hasVideo=self.data?has_content || self.dataUrl?has_content />
<#assign videoLink = bp.getVideoLink(self) />

<#-- just render a link -->
<@cm.include self=self view="asLink"  params={"attr": "data-cm-popup", "link": videoLink} />