<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextProductTeasable" -->

<#assign isLast=cm.localParameter("islast", false)/>
<@cm.include self=self view="teaser" params={
  "blockClass": "cm-hero",
  "additionalClass": "cm-hero--product cm-teaser--hero",
  "renderTeaserText": false,
  "renderDimmer": false
}/>
