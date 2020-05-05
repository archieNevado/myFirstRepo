<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMCollection" -->

<#if self.teaserText?has_content>
  <h2 class="cm-carousel-banner-container__headline"<@preview.metadata "properties.teaserTitle"/>>${self.teaserTitle}</h2>
</#if>
