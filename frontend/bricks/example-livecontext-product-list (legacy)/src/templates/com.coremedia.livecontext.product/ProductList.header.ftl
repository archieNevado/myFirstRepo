<#-- @ftlvariable name="self" type="com.coremedia.livecontext.product.ProductList" -->

<#if self.category?has_content>
<div class="cm-category">
    <h2 class="cm-category__headline">${self.category.name!""}</h2>
</div>
</#if>
