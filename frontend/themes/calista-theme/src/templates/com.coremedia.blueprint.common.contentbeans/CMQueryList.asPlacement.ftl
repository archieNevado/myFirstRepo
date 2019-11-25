<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMQueryList" -->

<#--
    Template Description:

    This template renders a CMQueryList with the view "asPlacement", which is used if a CMQueryList is added to a
    PageGridPlacement without layout variant. If pagination is enabled this template redirects to Pagination.asTeaser.ftl
    otherwise it renders the items as Container.asPlacement.ftl does.

    @since 1901
-->

<#assign isPaginated=self.isPaginated()!false />
<#assign modifierClass=isPaginated?then("cm-container--paginated", "") />

<div class="cm-container container ${modifierClass}"<@preview.metadata self.content />>
  <#if isPaginated>
    <@cm.include self=self.asPagination() view="asTeaser" />
  <#else>
    <#list self.items![] as item>
      <@cm.include self=item view="asTeaser" params={"renderAuthors": true, "renderDate": true}/>
    </#list>
  </#if>
</div>
