<#-- @ftlvariable name="self" type="com.coremedia.livecontext.commercebeans.ProductInSite" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->

<#assign cssClass=cm.localParameters().cssClass!"" />

<@bp.optionalLink attr={"class" : cssClass} href="${cm.getLink(self)}">${(self.product.name)!""}</@bp.optionalLink>