<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#-- redirect to view text but we need to create a container for this teasable before. -->
<@cm.include self=bp.getContainer([self]) view="asPlacement[text]" />
