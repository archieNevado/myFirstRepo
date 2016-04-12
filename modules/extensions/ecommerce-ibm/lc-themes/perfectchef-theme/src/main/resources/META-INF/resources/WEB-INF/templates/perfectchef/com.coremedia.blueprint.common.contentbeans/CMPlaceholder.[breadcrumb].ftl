<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPlaceholder" -->
<#-- @ftlvariable name="navigation" type="com.coremedia.blueprint.common.navigation.Navigation" -->
<#-- @ftlvariable name="lcNavigation" type="com.coremedia.livecontext.navigation.LiveContextCategoryNavigation" -->

<#if lcNavigation?has_content>
  <#assign navigation=lcNavigation />
<#else>
  <#assign navigation=cmpage.navigation />
</#if>
<@cm.include self=navigation!cm.UNDEFINED view="asBreadcrumb"
  params={
    "classBreadcrumb": "cm-placement-header__breadcrumb",
    "metadata": [self.content, "properties.id"]
  } />
