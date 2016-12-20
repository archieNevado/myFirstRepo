<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMCollection" -->
<#list self.items![] as item>
  <@cm.include self=item view="asTeaser"/>
</#list>
