<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->
<#assign renderDiv=(cm.localParameters().renderDiv!true && self.name != "footer" && self.name != "header") />

<#if self.items?has_content || self.name == "main" || self.name == "header">
  <#if renderDiv>
  <div id="cm-placement-${self.name!""}"
       class="cm-placement cm-placement-${self.name!""} col-xs-12 col-md-${self.colspan!1}"<@preview.metadata data=[bp.getPlacementPropertyName(self)!"", lc.fragmentHighlightingMetaData(self)!""]/>>
  </#if>

  <#-- replace main section with the main content to render -->
  <#if self.name! == "main" && cmpage.detailView>
  <div class="container">
    <div class="row">
      <div class="col-xs-12 col-md-10 col-lg-8 col-md-offset-1 col-lg-offset-2">
        <@cm.include self=cmpage.content/>
      </div>
    </div>
  </div>
  <#-- render the placement items -->
  <#elseif self.name! == "header">
    <@cm.include self=self view="asHeader"/>
  <#elseif self.name! == "footer">
    <@cm.include self=self view="asFooter"/>
  <#elseif self.name! == "footer-navigation">
    <@cm.include self=self view="asFooterNavigation"/>
  <#-- default -->
  <#else>
    <@cm.include self=self view="asPlacement" params={"additionalClass": "cm-container"} />
  </#if>

  <#if renderDiv!true>
    </div>
  </#if>
</#if>
