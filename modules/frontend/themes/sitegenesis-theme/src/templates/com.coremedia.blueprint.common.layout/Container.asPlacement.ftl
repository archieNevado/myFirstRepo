<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<div<@cm.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
<#list self.items![] as item>
  <@cm.include self=item view="asTeaser"/>
</#list>
</div>
