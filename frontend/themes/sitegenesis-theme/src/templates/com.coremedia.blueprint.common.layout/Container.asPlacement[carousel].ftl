<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#import "*/node_modules/@coremedia/slick-carousel/src/freemarkerLibs/slickCarousel.ftl" as slickCarousel />

<@slickCarousel.render items=self.flattenedItems
                       itemsView="asHero"
                       innerArrows=true
                       additionalClass=cm.localParameters().additionalClass!""
                       metadata=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />
