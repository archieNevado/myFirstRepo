<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMLinkable" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->
<#-- @ftlvariable name="enabled" type="java.lang.Boolean" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->
<#-- @ftlvariable name="metadata" type="java.lang.String" -->

<#assign additionalClass=cm.localParameter("additionalClass", "") />
<#assign enabled=cm.localParameter("enabled", false) />
<#assign text=cm.localParameter("text", "") />
<#assign metadata=cm.localParameter("metadata", "") />
<#-- cannot use default of bp.setting here as it only applies if the setting is not defined (not if set to "") -->
<#if !text?has_content>
  <#assign text=bp.getMessage("button_read_more") />
</#if>
<#assign target=self.openInNewTab?then("_blank", "_self") />
<#assign rel=self.openInNewTab?then("noopener", "") />

<#if enabled>
  <#assign attr={
    "class": "cm-cta-button ${additionalClass}",
    "role": "button",
    "target": target,
    "rel": rel,
    "metadata": metadata
  } />
  <@bp.optionalLink href="${cm.getLink(self)}" attr=attr>${text}</@bp.optionalLink>
</#if>
