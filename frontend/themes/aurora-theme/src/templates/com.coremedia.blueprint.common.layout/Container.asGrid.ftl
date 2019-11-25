<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#assign viewItems=cm.localParameter("viewItems", "asTeaser") />
<#assign items=self.flattenedItems![] />
<#assign numberOfItems=items?size />
<#assign itemsPerRow=cm.localParameter("itemsPerRow", 3) />
<#assign itemsPerMobileRow=cm.localParameter("itemsPerMobileRow", 1) />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign viewItemCssClass=cm.localParameters().viewItemCssClass!"" />
<#assign columnCssClass=cm.localParameters().columnCssClass!"" />
<#assign center=cm.localParameters().center!true />
<#assign showHeadline=cm.localParameters().showHeadline!true />

<div class="cm-grid cm-container cm-grid--${itemsPerRow}-per-row ${additionalClass}" <@preview.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
  <#if showHeadline && self.teaserTitle?has_content>
    <h2 class="cm-container__headline" <@preview.metadata data=[self.content,"properties.teaserTitle"]/>><span>${self.teaserTitle}</span></h2>
  </#if>
  <#if (numberOfItems > 0)>
      <#list items as item>
          <div class="cm-grid__item ${columnCssClass}">
            <@cm.include self=item view=viewItems params={"islast": item?is_last, "cssClass": viewItemCssClass} />
          </div>
      </#list>
  </#if>
</div>
