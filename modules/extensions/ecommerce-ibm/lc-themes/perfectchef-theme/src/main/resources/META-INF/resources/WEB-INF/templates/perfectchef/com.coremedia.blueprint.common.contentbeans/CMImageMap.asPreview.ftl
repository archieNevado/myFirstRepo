<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign fragmentViews=[
  {
    "viewName": "DEFAULT",
    "titleKey": "Preview_Label_Default"
  }, {
    "viewName": "asTeaserHero",
    "titleKey": "Preview_Label_Teaser_Hero"
  }, {
    "viewName": "asTeaser",
    "titleKey": "Preview_Label_Teaser"
  }] />

<@cm.include self=self view="multiViewPreview" params={
  "fragmentViews": fragmentViews
}/>
