<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->
<div class="container-fluid"<@preview.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
<@cm.include self=self view="asGrid" params={"itemsPerRow": 2, "additionalClass": "cm-collection--tiles-50-50-portrait"} />
</div>
