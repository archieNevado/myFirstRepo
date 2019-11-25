<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="items" type="java.util.List" -->
<#-- @ftlvariable name="viewItems" type="java.lang.String" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#assign blockClass=cm.localParameters().blockClass!"cm-container" />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign viewItems=cm.localParameters().viewItems!"teaser" />
<#assign items=self.flattenedItems![] />
<#-- load optional configuration from content for the number of slides, first check the collection itself, than for a default on the page -->
<#assign carouselConfig=bp.setting(self, "responsiveCarousel", bp.setting(self, "responsiveCarousel", "")) />
<#if carouselConfig?has_content>
  <#assign config='{"slidesToShowDesktop": ${carouselConfig.slidesToShowDesktop!""}, "slidesToShowMobile": ${carouselConfig.slidesToShowMobile!""}}' />
</#if>

<#if items?has_content>
  <div class="${blockClass} ${additionalClass} cm-responsive-carousel" <@preview.metadata bp.getContainerMetadata(self) />>
    <#-- render title only for collections (placements do not have a title) -->
    <#if self.teaserTitle?has_content>
      <h2 class="${blockClass}__headline"<@preview.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h2>
    </#if>
    <div class="cm-responsive-carousel__inner" data-cm-responsive-carousel="${config!""}" <@preview.metadata bp.getPlacementHighlightingMetaData(self)!""/>>
      <#list items as item>
          <#-- this div is used by slick for the slides -->
          <div>
            <#-- include the items with view "teaser" from brick "default-teaser"
                 but hide the dimmer and teaserText (overwrite defaults) -->
            <@cm.include self=item view=viewItems params={
              "additionalClass": "cm-responsive-carousel__item",
              "renderDimmer": false,
              "renderTeaserText": false
            }/>
          </div>
      </#list>
    </div>
  </div>
</#if>
