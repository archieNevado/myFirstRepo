<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#--
    Template Description:

    If pagination is enabled, it delegates to the Pagination object with the template Pagination.ftl,
    otherwise it redirects to the default view "asRelated".

    @since 1901
-->

<#assign blockClass=cm.localParameters().blockClass!"cm-related"/>
<#assign relatedView=cm.localParameters().relatedView!"asRelated" />

<div class="${blockClass}" <@preview.metadata self.content />>
  <#-- headline -->
  <#if self.teaserTitle?has_content>
    <h2 class="${blockClass}__headline"<@preview.metadata "properties.teaserTitle"/>>
      <span>${self.teaserTitle}</span>
    </h2>
  </#if>

  <#--items paginated or "asRelated" -->
  <#if self.isPaginated()!false>
    <@cm.include self=self.asPagination() />
  <#else>
    <@cm.include self=self view=relatedView />
  </#if>
</div>
