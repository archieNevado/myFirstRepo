<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#import "*/node_modules/@coremedia/slick-carousel/src/freemarkerLibs/slickCarousel.ftl" as slickCarousel />

<#assign blockClass=cm.localParameters().blockClass!"cm-details" />

<@slickCarousel.render items=self.media
                       itemsView="_header"
                       itemsParams={
                         "renderTitle": false,
                         "renderText": false
                       }
                       innerArrows=true
                       additionalClass="${blockClass}__medias"
                       metadata="pictures" />
