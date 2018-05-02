<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<div class="container cm-container cm-container--hero"<@preview.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
  <div class="row">
  <#if self.items?has_content>
    <div class="col-xs-12">
      <@cm.include self=self view="asCarousel" params={"modifier": "hero", "controlIcon": "triangle", "viewItems": "asHero", "displayPagination": false} />
    </div>
  </#if>
  </div>
</div>
