package com.coremedia.livecontext.ecommerce.ibm.catalog;

import java.util.Map;

/**
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated
class ProductDelegateLoader extends AbstractDelegateLoader {

  private final ProductBase product;

  ProductDelegateLoader(ProductBase product) {
    this.product = product;
  }

  @Override
  Map<String, Object> getDelegateFromCache() {
    return product.getDelegateFromCache();
  }
}
