<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->
<#-- @ftlvariable name="cmpage" type="com.coremedia.blueprint.common.contentbeans.Page" -->

<#-- workaround for coremedia widget, will be removed with CMS-1482 -->
<!-- CoreMedia Placement ${self.name!""} -->
<div class="cm-placement-${self.name!""}" <@cm.metadata data=[bp.getPageMetadata(cmpage)!"", bp.getPlacementPropertyName(self)!"",lc.fragmentHighlightingMetaData(self)!""] />>
  <@cm.include self=self view="asPlacement" params={"additionalClass": "cm-container"} />
</div>