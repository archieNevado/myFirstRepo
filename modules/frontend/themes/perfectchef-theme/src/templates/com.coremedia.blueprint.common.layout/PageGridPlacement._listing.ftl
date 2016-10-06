<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->
<#assign renderDiv=cm.localParameters().renderDiv!true />

<div id="cm-placement-${self.name!""}"
     class="cm-placement-${self.name!""} col-xs-12 col-md-${self.colspan!1}"<@cm.metadata data=bp.getPlacementPropertyName(self)!""/>>
  <@cm.include self=self params={"renderDiv": false}/>
</div>