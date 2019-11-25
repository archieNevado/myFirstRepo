<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#import "*/node_modules/@coremedia/slick-carousel/src/freemarkerLibs/slickCarousel.ftl" as slickCarousel />

<div class="container cm-container cm-container--hero"<@preview.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
  <div class="row">
  <#if self.items?has_content>
    <div class="col-xs-12">
      <@slickCarousel.render items=self.flattenedItems
                             itemsView="asHero"
                             innerArrows=true />
    </div>
  </#if>
  </div>
</div>
