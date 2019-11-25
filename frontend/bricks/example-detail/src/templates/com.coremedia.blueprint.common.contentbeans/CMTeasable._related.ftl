<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#--
    Template Description:

    This template renders elements from the related property in a section with the given view "relatedView".
    This is by default "asRelated". The items are rendered via getDynamizableContainer() for personalization as Container
    in Container.asRelated.ftl.
-->

<#assign blockClass=cm.localParameters().blockClass!"cm-related"/>
<#assign additionalClass=cm.localParameters().additionalClass!""/>
<#assign relatedView=cm.localParameters().relatedView!"asRelated" />

<#if self.related?has_content>
  <section class="${blockClass} ${additionalClass}" <@preview.metadata "properties.related"/>>
    <#-- headline -->
    <h3 class="${blockClass}__headline"><@cm.message key="related_label"/></h3>

    <#--items -->
    <@cm.include self=bp.getDynamizableContainer(self, "related") view=relatedView />
  </section>
</#if>
