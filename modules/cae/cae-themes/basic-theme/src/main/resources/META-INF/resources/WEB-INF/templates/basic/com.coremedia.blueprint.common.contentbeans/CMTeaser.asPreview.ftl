<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeaser" -->

<#assign fragmentViews=[
  {
    "viewName": "asTeaser",
    "titleKey": "Preview_Label_Teaser"
  }] />

<@cm.include self=self view="multiViewPreview" params={
  "fragmentViews": fragmentViews
}/>
