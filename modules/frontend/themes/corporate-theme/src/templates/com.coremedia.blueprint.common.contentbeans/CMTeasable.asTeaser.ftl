<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign index=cm.localParameters().index!0 />
<#assign additionalClasses=[] />
<#if index % 2 == 0>
  <#assign additionalClasses+=["cm-teasable--corporate-identity"] />
</#if>
<#if !(self.picture?has_content)>
  <#assign additionalClasses+=["cm-teasable--no-image"] />
</#if>

<@cm.include self=self view="teaser" params={
  "additionalClass": additionalClasses?join(" "),
  "renderDimmer": false,
  "enableTeaserOverlay": false
}/>
