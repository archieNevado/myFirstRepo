<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMLinkable" -->
<#-- @ftlvariable name="cmpage" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#--this template simply delegates, but is still necessary since fragmentHandler resolves to linkable-->
<#-- Breadcrumb above Pagegrid for IBM -->
<#if lc.getVendorName() == "IBM">
<div class="cm-breadcrumb--outer">
  <@cm.include self=cmpage.navigation!cm.UNDEFINED view="asBreadcrumbFragment"/>
</div>
</#if>
<@cm.include self=self view="detail" params={"relatedView": "asRelated", "renderTags": false}/>
