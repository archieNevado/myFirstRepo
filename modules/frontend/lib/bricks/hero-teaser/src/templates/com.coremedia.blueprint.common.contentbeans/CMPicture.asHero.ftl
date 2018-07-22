<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->

<#-- render teaser with default settings -->
<@cm.include self=self view="hero" params={
  "renderLink": false
}/>