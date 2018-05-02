<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#assign viewItems=cm.localParameter("viewItems", "asTeaser") />
<#assign items=self.flattenedItems![] />
<#assign numberOfItems=items?size />
<#assign itemsPerRowXS=cm.localParameter("itemsPerRowXS", 1) />
<#assign itemsPerRowSM=cm.localParameter("itemsPerRowSM", 0) />
<#assign itemsPerRowMD=cm.localParameter("itemsPerRowMD", 0) />
<#assign itemsPerRowLG=cm.localParameter("itemsPerRowLG", 0) />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign viewItemCssClass=cm.localParameters().viewItemCssClass!"" />
<#assign columnCssClass=cm.localParameters().columnCssClass!"" />
<#assign center=cm.localParameters().center!true />
<#assign showHeadline=cm.localParameters().showHeadline!true />

<div class="cm-container ${additionalClass}" <@preview.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
  <#if showHeadline && self.teaserTitle?has_content>
    <h2 class="cm-container__headline" <@preview.metadata data=[self.content,"properties.teaserTitle"]/>><span>${self.teaserTitle}</span></h2>
  </#if>
  <#if (numberOfItems > 0)>
    <div class="row-grid cm-flex-row <#if center>cm-flex-row--center</#if>" <#if self.content?has_content><@preview.metadata data=[self.content,"properties.items"]/></#if>>
      <#list items as item>
          <div class="${columnCssClass} cm-flex-col-xs-${12/itemsPerRowXS} <#if (itemsPerRowSM>0)>cm-flex-col-sm-${12/itemsPerRowSM}</#if> <#if (itemsPerRowMD>0)>cm-flex-col-md-${12/itemsPerRowMD}</#if> <#if (itemsPerRowLG>0)>cm-flex-col-lg-${12/itemsPerRowLG}</#if>">
            <@cm.include self=item view=viewItems params={"islast": item?is_last, "cssClass": viewItemCssClass} />
          </div>
      </#list>
    </div>
  </#if>
</div>