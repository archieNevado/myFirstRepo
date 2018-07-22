<#--
  Renders an addToCart button in different variations depending on the given product. If product has only
  one product variant the button includes an add to cart functionality, otherwise its just a link to the product
  detail page. If no product is available, it renders an unavailable labeled button.

  @param product The product that needs to be added to the cart.
  @param alwaysShow Forces the button to be shown even if product is not available.
  @param alwaysClickable Enables add to cart functionality even if there is more than one variant.
  @param enableShopNow Enable addToCart functionality. If false, only show the Details button.
  @param withLink Link to the product detail page. Will be used for the button, if enableShopNow is
                  disabled or product has more than one variant.
  @param attr Adds additional custom attributes as a hash map to the button.

  Example:
  <@addToCartButton product=self.product
                    withLink=cm.getLink(self.productInSite)
                    attr={"classes":["my-button-group__button", "my-button--linked"]} />

  Note:
  For now uses the deprecated function to avoid code duplication as we cannot depend from "lc-cae" to
  the frontend workspace.
-->
<#macro addToCartButton product alwaysShow=false alwaysClickable=false enableShopNow=true withLink="" attr={}>
  <@lc.addToCartButton product=product
                       alwaysShow=alwaysShow
                       alwaysClickable=alwaysClickable
                       enableShopNow=enableShopNow
                       withLink=withLink
                       attr=attr />
</#macro>
