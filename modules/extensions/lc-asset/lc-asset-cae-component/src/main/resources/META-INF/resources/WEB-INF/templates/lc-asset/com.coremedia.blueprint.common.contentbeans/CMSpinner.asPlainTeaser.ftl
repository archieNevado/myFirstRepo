<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMSpinner" -->

<#if self.picture?has_content>
  <div class="cm-teaser cm-teaser--spinner cm-teaser--plain">
    <@cm.include self=self.picture />
    <div class="cm-spinner__icon"></div>
  </div>
</#if>
