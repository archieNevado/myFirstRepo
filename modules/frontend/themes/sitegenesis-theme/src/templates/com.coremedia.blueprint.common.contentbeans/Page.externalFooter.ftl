<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/>
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#-- @ftlvariable name="previewFacade" type="com.coremedia.objectserver.view.freemarker.PreviewFacade" -->

<#assign sliderMetadata = previewFacade.metadata(bp.setting(self, "sliderMetaData", ""))>
<div style="visibility: hidden;" data-cm-metadata="${sliderMetadata}"></div>

<#-- same as in cms-only pages -->
<@cm.include self=self view="_bodyEnd" />
