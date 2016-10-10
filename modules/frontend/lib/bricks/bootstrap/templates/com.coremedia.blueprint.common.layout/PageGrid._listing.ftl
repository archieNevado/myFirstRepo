<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGrid" -->

<#list self.rows![] as row>
  <#if row.hasItems>
    <#assign cssClass=""/>
    <#if ((row.placements![])?size > 1)>
      <#assign cssClass="cm-multicolumn"/>
    </#if>
    <div class="cm-row row ${cssClass!""}">
      <#-- Iterate over each placement-->
      <#list row.placements![] as placement>
        <#if placement.items?has_content || (placement.name == "main")>
          <@cm.include self=placement view="_listing" />
        </#if>
      </#list>
    </div>
  </#if>
</#list>
