<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGrid" -->

<#if self?has_content>
  <div class="cm-grid container-fluid ${self.cssClassName!""}">
    <@cm.include self=self view="_listing"/>
  </div>
</#if>
