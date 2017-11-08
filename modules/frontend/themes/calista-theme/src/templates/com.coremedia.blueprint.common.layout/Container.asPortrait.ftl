<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<@cm.include self=self view="asGrid" params={
  "additionalClass": "container cm-portrait-teaser",
  "itemsPerRowXS": 2,
  "itemsPerRowSM": 3,
  "itemsPerRowMD": 6,
  "showHeadline": true,
  "viewItems": "asPortrait"
}/>
