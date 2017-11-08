<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign renderLink=cm.localParameter("renderLink", true) />

<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign target=(self.target?has_content && self.target.openInNewTab)?then(' target="_blank"', "") />

<#assign ctaDisabled=bp.setting(self, "callToActionDisabled", false) />
<#assign ctaText=bp.setting(self, "callToActionCustomText", "") />
<!-- cannot use default of bp.setting here as it only applies if the setting is not defined (not if set to "") -->
<#if !ctaText?has_content>
  <#assign ctaText=bp.getMessage("button_read_more") />
</#if>

<#if link?has_content && !ctaDisabled>
  <#if renderLink>
    <a class="cm-button ${additionalClass} btn btn-default" href="${link}"${target?no_esc} role="button">${ctaText}</a>
  <#else>
    <button class="cm-button ${additionalClass} btn btn-default">${ctaText}</button>
  </#if>
</#if>
