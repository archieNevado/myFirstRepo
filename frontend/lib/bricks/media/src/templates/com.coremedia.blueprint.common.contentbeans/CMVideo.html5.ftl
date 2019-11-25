<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->

<#-- DEPRECATED: use view "media" instead -->
<@cm.include self=self view="media" params={
  <#-- for compatibility, private parameter _renderBox will be removed with the next AEP -->
  "_renderBox": false,
  <#-- delegate deprecated parameter classVideo to classMedia -->
  "classMedia": cm.localParameters().classVideo!"",
  <#-- change metadata template parameter to metadataMedia as box is not rendered -->
  "metadataMedia": (cm.localParameters().metadata)![] + [self.content]
} + cm.localParameters() />
