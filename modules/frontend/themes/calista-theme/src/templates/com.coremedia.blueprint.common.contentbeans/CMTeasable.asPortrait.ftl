<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign isLast=cm.localParameter("islast", false)/>
<@cm.include self=self view="teaser" params={
  "additionalClass": "cm-portrait-teaser cm-teasable",
  "isLast": isLast,
  "renderTeaserText": true,
  "renderEmptyImage": false,
  "renderCTA": false,
  "renderDimmer": false
}/>
