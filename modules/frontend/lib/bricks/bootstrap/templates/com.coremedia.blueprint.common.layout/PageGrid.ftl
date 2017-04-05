<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGrid" -->

<#if self?has_content>
  <#assign gridModifier="" />
  <#assign superheroPlacement=bp.getPlacementByName("hero", self)!cm.UNDEFINED />
  <#if ((superheroPlacement.viewTypeName)?has_content && superheroPlacement.viewTypeName == "superhero" && superheroPlacement.getItems()?size > 0)>
    <#assign gridModifier="cm-grid--with-superhero" />
  </#if>
  <div class="cm-grid ${(self.pageGrid.cssClassName)!""} ${gridModifier} container-fluid">
    <@cm.include self=self view="_listing"/>
  </div>
</#if>
