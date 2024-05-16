<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->

<#list cmpage.navigation.navigationPathList>
  <#items as navigation>
  <@cm.include self=navigation view="asLink" params={"cssClass":"breadcrumb-element"}/>
  </#items>
</#list>

