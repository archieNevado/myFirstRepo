<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign ctaDisabled=bp.setting(self, "callToActionDisabled", false) />
<#assign ctaLabel=bp.setting(self, "callToActionCustomText", "") />
<#assign additionalClass=cm.localParameters().additionalClass!"" />

<#if link?has_content>
  <#if (!ctaDisabled && ctaLabel != "")>
  <a class="cm-button ${additionalClass} btn btn-default" href="${link}" role="button">
  ${ctaLabel}
  </a>
  <#-- default call-to-action button -->
  <#elseif (!ctaDisabled)>
  <a class="cm-button ${additionalClass} btn btn-default" href="${link}" role="button">
  ${bp.getMessage("button_read_more")}
  </a>
  </#if>
</#if>