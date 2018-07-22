<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->
<#-- @ftlvariable name="classMedia" type="java.lang.String" -->

<#assign hasVideo=self.data?has_content || self.dataUrl?has_content />

<#if hasVideo>
  <@cm.include self=self view="media" params={
    "classBox": classBox!"",
    "classMedia": classMedia!"",
    "classMedia": "cm-details__video"
  } />
</#if>
