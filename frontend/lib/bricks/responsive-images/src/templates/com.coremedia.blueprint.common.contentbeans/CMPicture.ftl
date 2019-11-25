<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->

<#-- DEPRECATED, use view "media" instead -->
<@cm.include self=self view="media" params={
  "classMedia": cm.localParameters().classImage!""
} + cm.localParameters()/>
