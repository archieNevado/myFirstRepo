<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->
<#-- @ftlvariable name="additionalButtonClass" type="java.lang.String" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<#-- DEPRECATED: use cta.render provided by ftl-utils instead -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/cta.ftl" as cta />

<#assign additionalClass=cm.localParameter("additionalClass", "") />
<#assign additionalButtonClass=cm.localParameter("additionalButtonClass", "") />
<#assign metadata=cm.localParameter("metadata", []) />

<@cta.render buttons=self.callToActionSettings
             additionalClass=additionalClass
             additionalButtonClass=additionalButtonClass
             metadata=metadata />
