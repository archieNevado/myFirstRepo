<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/>
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->

<#assign context=self.context>
<#assign augmentedContent = lc.augmentedContent()>

<#if !augmentedContent>
  <!--CM { "objectType":"page","renderType":"metadata","title":"","description":"","keywords":"","pageName":"" } CM-->
<#else>
  <!--CM { "objectType":"page","renderType":"metadata","title":"${context.htmlTitle}","description":"${context.htmlDescription}",
  "keywords":"${context.keywords}","pageName":"${context.title}" } CM-->
</#if>