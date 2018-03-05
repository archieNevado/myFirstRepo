<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->
<div class="container-fluid"<@cm.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
<@cm.include self=self view="asGrid" params={"itemsPerRow": 3, "additionalClass": "cm-collection--tiles-landscape"} />
</div>
