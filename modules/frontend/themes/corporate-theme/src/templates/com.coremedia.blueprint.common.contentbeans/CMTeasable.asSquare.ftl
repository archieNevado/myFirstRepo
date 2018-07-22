<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign isLast=cm.localParameter("islast", false)/>
<#assign additionalClass=cm.localParameter("cssClass", "")/>

<@cm.include self=self view="teaser" params={
  "additionalClass": additionalClass,
  "isLast": isLast,
  "blockClass": "cm-square"
}/>
