<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.catalog.Product" -->

<#-- @deprecated since 2007.1 -->
<#if (self.totalStockCount > 0) >
  ${self.totalStockCount}
<#else>
  N/A
</#if>
