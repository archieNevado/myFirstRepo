<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPlaceholder" -->

<#assign substitution=cm.substitute(self.id!"", cmpage) />
<@cm.include self=substitution view="listing" />
