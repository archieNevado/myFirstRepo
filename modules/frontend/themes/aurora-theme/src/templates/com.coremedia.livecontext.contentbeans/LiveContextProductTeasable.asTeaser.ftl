<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextProductTeasable" -->

<#assign isLast=cm.localParameter("islast", false)/>
<@cm.include self=self view="teaser" params={
  "additionalClass": "cm-teasable--product",
  "isLast": isLast,
  "renderTeaserText": false,
  "renderDimmer": false
}/>
