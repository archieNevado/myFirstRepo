<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<@cm.include self=bp.getContainer(self.flattenedItems) view="asGrid" params={
  "itemsPerRow": 3,
  "additionalClass": "cm-collection--tiles-landscape cm-related__items",
  "columnCssClass": "cm-related__item"
} />
