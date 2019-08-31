<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#import "*/node_modules/@coremedia/brick-slick-carousel/src/freemarkerLibs/slickCarousel.ftl" as slickCarousel />

<@slickCarousel.render items=self.items
                       itemsView="asHero"
                       innerArrows=true
                       additionalClass=cm.localParameters().additionalClass!""
                       metadata=bp.getContainerMetadata(self) + [bp.getPlacementHighlightingMetaData(self)!""] />
