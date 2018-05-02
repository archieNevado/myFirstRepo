<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#assign link=cm.getLink(self!cm.UNDEFINED)/>
<#assign additionalCssClass=cm.localParameter("additionalCssClass","hidden")/>
<#assign target=self.openInNewTab?then('target="_blank"', "") />
<#assign rel=self.openInNewTab?then('rel="noopener"', "") />

<li<#if additionalCssClass?has_content> class="${additionalCssClass}"</#if> <@preview.metadata self.content />>
  <a href="${link}" ${target?no_esc} ${rel?no_esc} class="departmentButton" role="menuitem">
    <span>${self.teaserTitle}</span>
  </a>
</li>