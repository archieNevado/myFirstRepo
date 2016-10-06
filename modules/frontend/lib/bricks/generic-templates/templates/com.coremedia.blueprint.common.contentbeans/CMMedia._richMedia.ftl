<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMMedia" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.util.List" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->
<#-- @ftlvariable name="classImage" type="java.lang.String" -->

<@cm.include self=self params={
  "limitAspectRatios": limitAspectRatios![],
  "classBox": classBox!"",
  "classImage": classImage!""
}/>