<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->
<#-- @ftlvariable name="cmpage" type="com.coremedia.blueprint.common.contentbeans.Page" -->

<#-- workaround for coremedia widget, will be removed with CMS-1482 -->
<!-- CoreMedia Placement ${self.name!""} -->
<div class="cm-placement-${self.name!""}" <@cm.metadata data=[bp.getPlacementPropertyName(self)!"", lc.fragmentHighlightingMetaData(self)!""] />>
  <#if self.items?has_content>
      <#list self.items![] as item>
          <@cm.include self=item view="asTeaser" />
      </#list>
  </#if>
</div>
