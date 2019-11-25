<#-- @ftlvariable name="self" type="com.coremedia.livecontext.commercebeans.ProductInSite" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->

<#import "*/node_modules/@coremedia/ftl-utils/src/freemarkerLibs/utils.ftl" as utils />

<#assign cssClass=cm.localParameters().cssClass!"" />

<@utils.optionalLink attr={"class" : cssClass} href="${cm.getLink(self)}">${(self.product.name)!""}</@utils.optionalLink>