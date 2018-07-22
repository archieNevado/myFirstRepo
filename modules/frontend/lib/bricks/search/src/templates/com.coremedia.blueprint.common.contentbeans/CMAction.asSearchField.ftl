<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMAction" -->

<#-- delegate to SearchActionState.asSearchfield.ftl using Substitution API -->
<#assign substitution=cm.substitute(self.id!"", self) />
<@cm.include self=substitution view="asSearchField" />
