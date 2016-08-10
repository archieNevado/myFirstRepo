<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->
<@cm.include self=self view="asTeaser" params={"metadata": (metadata![]) + [self.content, "properties.items"]} />
