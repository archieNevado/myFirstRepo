<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/>
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->

<#assign content=self.content>
<#assign augmentedContent = lc.augmentedContent()>

<#if !augmentedContent>
  <!--CM { "objectType":"page","renderType":"metadata","title":"","description":"","keywords":"","pageName":"" } CM-->
<#else>
  <!--CM { "objectType":"page","renderType":"metadata","title":"${content.htmlTitle}","description":"${content.htmlDescription}",
  "keywords":"${content.keywords}","pageName":"${content.title}" } CM-->
</#if>