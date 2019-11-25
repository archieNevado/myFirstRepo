<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#import "*/node_modules/@coremedia/slick-carousel/src/freemarkerLibs/slickCarousel.ftl" as slickCarousel />

<#if self.items?has_content>
  <div class="cm-container cm-container--fullwidth" <@preview.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
    <@slickCarousel.render items=self.flattenedItems
                           itemsView="asHero"
                           innerArrows=true />
  </div>
</#if>
