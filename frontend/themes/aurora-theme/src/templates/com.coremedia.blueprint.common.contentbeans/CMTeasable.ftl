<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<@cm.include self=self view="asBreadcrumbFragment"/>
<@cm.include self=self view="detail" params={"relatedView": "asRelated", "renderTags": false}/>
