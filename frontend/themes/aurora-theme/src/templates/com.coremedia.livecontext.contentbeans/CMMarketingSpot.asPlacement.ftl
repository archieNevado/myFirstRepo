<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.CMMarketingSpot" -->

<#if self?has_content && self.externalId?has_content && self.items?has_content>
  <#assign items=self.items![] />
  <#assign numberOfItems=items?size />

  <#-- there is at least 1 marketing spot, so zero does not need to be defined -->
  <#assign itemsPerRow=3 />
  <#switch self.items?size>
    <#case 1>
      <#assign itemsPerRow=1 />
      <#break>
    <#case 2>
      <#assign itemsPerRow=2 />
      <#break>
  </#switch>
  <div class="cm-collection--marketingspot">
    <div class="cm-grid cm-grid--${itemsPerRow}-per-row" <@preview.metadata data=bp.getContainerMetadata(self) + [bp.getPlacementHighlightingMetaData(self)!""] />>
      <#if (numberOfItems > 0)>
          <#list items as item>
              <div class="cm-grid__item">
                <@cm.include self=item view="teaser" />
              </div>
          </#list>
      </#if>
    </div>
  </div>
</#if>
