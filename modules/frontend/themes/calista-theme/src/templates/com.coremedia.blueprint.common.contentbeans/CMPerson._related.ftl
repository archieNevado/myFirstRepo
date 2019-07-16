<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPerson" -->

<#--
    Template Description:

    This template renders elements from the "related" property with the view "relatedIem"
    in a section without a headline for CMPerson.

    @since 1901
-->

<#assign blockClass=cm.localParameters().blockClass!"cm-related"/>
<#assign additionalClass=cm.localParameters().additionalClass!""/>
<#assign relatedView=cm.localParameters().relatedView!"asRelated" />

<section class="${additionalClass}" <@preview.metadata "properties.related"/>>
  <#--items in related property -->
  <#list self.related![] as item>
    <@cm.include self=item view="asRelatedItem" params={"blockClass": blockClass, "relatedView": relatedView} />
  </#list>
</section>
