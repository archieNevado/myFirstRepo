<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#--
    Template Description:
    This is a generic fallback template. Intended to be overwritten in the theme.
-->

<#list self.items as item>
  <div>${item.title}</div>
</#list>
