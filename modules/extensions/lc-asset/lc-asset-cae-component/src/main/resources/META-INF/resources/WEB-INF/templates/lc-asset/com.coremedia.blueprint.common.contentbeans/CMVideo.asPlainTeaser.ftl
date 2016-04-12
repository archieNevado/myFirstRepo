<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->

<#if self.picture?has_content>
  <div class="cm-teaser cm-teaser--video cm-teaser--plain">
    <@cm.include self=self.picture />
    <div class="cm-teaser--video__play"></div>
  </div>
</#if>