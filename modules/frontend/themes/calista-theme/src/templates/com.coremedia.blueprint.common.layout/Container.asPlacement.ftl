<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<div class="cm-container container"<@preview.metadata "properties.items"/>>
  <#list self.items![] as item>
    <@cm.include self=item view="asTeaser" params={"renderAuthors": true, "renderDate": true}/>
  </#list>
</div>
