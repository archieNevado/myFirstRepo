<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#assign link=cm.getLink(self!cm.UNDEFINED)/>
<#assign additionalCssClass=cm.localParameter("additionalCssClass","hidden")/>

<#assign additionalCssClass=cm.localParameter("additionalCssClass","hidden")/>

<li<#if additionalCssClass?has_content> class="${additionalCssClass}"</#if> <@cm.metadata self.content />>
  <a href="${link}" class="departmentButton" role="menuitem">
    <span>${self.teaserTitle}</span>
  </a>
</li>