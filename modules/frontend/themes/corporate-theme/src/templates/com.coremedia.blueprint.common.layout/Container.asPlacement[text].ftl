<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#import "*/node_modules/@coremedia/brick-bootstrap/src/freemarkerLibs/bootstrap.ftl" as bootstrap />

<#assign items=self.flattenedItems![] />
<#assign numberOfItems=items?size />
<#assign itemsPerRow=3 />

<div class="cm-container cm-container--text"<@preview.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
<#if (numberOfItems > 0)>
    <div class="cm-row--full-height row">
      <#list items as item>
      <#-- add new row -->
        <@bootstrap.renderNewRow item_index itemsPerRow "cm-row--full-height " />
      <#-- render the items as claim teaser -->
        <#assign offsetClass=bootstrap.getOffsetClass(item_index, numberOfItems, itemsPerRow, " col-sm-") />
          <div class="cm-col--full-height col-xs-12 col-sm-4${offsetClass}">
            <@cm.include self=item view="asText" params={"islast": item?is_last} />
          </div>
      </#list>
    </div>
</#if>
</div>
