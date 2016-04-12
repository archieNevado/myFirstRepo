<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMSpinner" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.util.List" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->
<#-- @ftlvariable name="classImage" type="java.lang.String" -->

<#if (self.sequence![])?size gt 2>
  <div class="cm-spinner__canvas">
    <ol class="cm-spinner__images ${classBox!""}"<@cm.metadata "properties.sequence"/>>
      <#list self.sequence as image>
        <li class="cm-spinner__image ${classImage}">
          <@cm.include self=image params={
            "limitAspectRatios": limitAspectRatios![],
            "classBox": classBox!"",
            "classImage": classImage!""
          }/>
        </li>
      </#list>
    </ol>
    <div class="cm-spinner__icon"></div>
  </div>
</#if>


