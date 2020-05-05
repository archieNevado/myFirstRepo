<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/>
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->

<#--
  Template Description:

  This template renders the items of a placement as square banner.
-->

<!-- CoreMedia Placement ${self.name!""} -->
<div class="cm-placement cm-placement--${self.name!""}"<@preview.metadata data=[bp.getPlacementPropertyName(self), bp.getPlacementHighlightingMetaData(self)!""]/>>
  <#list self.items as item>
    <@cm.include self=item view="asSquareBanner" />
  </#list>
</div>
