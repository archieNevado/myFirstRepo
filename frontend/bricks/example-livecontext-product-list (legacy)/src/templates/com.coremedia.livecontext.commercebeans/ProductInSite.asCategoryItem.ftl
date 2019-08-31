<#-- @ftlvariable name="self" type="com.coremedia.livecontext.commercebeans.ProductInSite" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->

<#import "*/node_modules/@coremedia/brick-utils/src/freemarkerLibs/utils.ftl" as utils />
<#import "*/node_modules/@coremedia-examples/brick-cart/src/freemarkerLibs/cart.ftl" as cart />


<#if self.product?has_content && (cmpage.navigation.rootNavigation)?has_content>
  <#if self.product.listPrice?has_content && self.product.currency?has_content && self.product.locale?has_content>
    <#assign listPriceFormatted=lc.formatPrice(self.product.listPrice, self.product.currency, self.product.locale)/>
  </#if>
  <div class="cm-category-item">
    <@utils.optionalLink href="${cm.getLink(self)}">
      <@cm.include self=self.product.catalogPicture!cm.UNDEFINED view="media" params={
        "classBox": "cm-category-item__image cm-teasable__picture-box",
        "classMedia": "cm-teasable__picture"
      } />

      <div class="cm-category-item__info">
        <#-- headline -->
        <h4 class="cm-category-item__title">${self.product.name!""}</h4>
        <#-- price -->
        <div class="cm-category-item__pricing">
          <@cm.include self=self.product view="pricing" params={
            "classListPrice": "cm-category-item__list-price cm-price--category-item",
            "classOfferPrice": "cm-category-item__offer-price cm-price--category-item"
          } />
        </div>
        <#-- add to cart button -->
        <div class="cm-category-item__cart">
          <div class="cm-button-group cm-button-group--linked">
            <@cart.addToCartButton product=self.product
                                   attr={"classes": ["cm-button-group__button", "cm-button--linked"]}
                                   token=_CSRFToken!"" />
          </div>
        </div>
      </div>
    </@utils.optionalLink>
  </div>
</#if>
