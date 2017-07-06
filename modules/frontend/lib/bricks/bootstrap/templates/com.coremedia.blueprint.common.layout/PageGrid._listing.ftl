<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGrid" -->

<#list self.rows![] as row>
    <#assign cssClass=""/>
    <#if ((row.placements![])?size > 1)>
      <#assign cssClass="cm-multicolumn"/>
    </#if>
    <div class="cm-row row ${cssClass!""}">
      <#-- Iterate over each placement-->
      <#list row.placements![] as placement>
        <#if placement.items?has_content || (placement.name == "main")>
          <@cm.include self=placement view="_listing" />
        <#else>
          <div class="col-xs-12 col-md-${self.colspan!1}" <@cm.metadata data=[bp.getPlacementPropertyName(placement)!"",bp.getPlacementHighlightingMetaData(placement)!""]/>></div>
        </#if>
      </#list>
    </div>
</#list>
