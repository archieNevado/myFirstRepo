<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->

<ul class="cm-navigation cm-navigation--footer cm-dropdown" data-dropdown-menus="ul" data-dropdown-items="li"
    data-dropdown-class-button-open="icon-menu-next"
    data-dropdown-class-button-close="icon-menu-back"<@cm.metadata data=[bp.getPageMetadata(cmpage)!"", bp.getPlacementPropertyName(self)!""] />>
  <#list self.items![] as item>
    <@cm.include self=item view="asLinkListItem" />
  </#list>
</ul>
