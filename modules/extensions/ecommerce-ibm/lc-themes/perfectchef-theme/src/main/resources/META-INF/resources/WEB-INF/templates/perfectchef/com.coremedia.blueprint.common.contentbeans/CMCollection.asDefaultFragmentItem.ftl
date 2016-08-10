<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMCollection" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->
<@cm.include self=self params={"metadata": (metadata![]) + [self.content, "properties.items"]}/>