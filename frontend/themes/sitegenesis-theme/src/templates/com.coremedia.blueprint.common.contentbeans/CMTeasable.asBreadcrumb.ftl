<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#list cmpage.navigation.navigationPathList>
  <#items as navigation>
  <@cm.include self=navigation view="asLink" params={"cssClass":"breadcrumb-element"}/>
  </#items>
</#list>
<@cm.include self=self view="asLink" params={"cssClass":"breadcrumb-element"}/>
