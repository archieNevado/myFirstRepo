package com.coremedia.livecontext.ecommerce.ibm.catalog;

import java.util.Map;

/**
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated
class CategoryDelegateLoader extends AbstractDelegateLoader {

  private final CategoryImpl category;

  public CategoryDelegateLoader(CategoryImpl category) {
    this.category = category;
  }

  @Override
  Map<String, Object> getDelegateFromCache() {
    return category.getDelegateFromCache();
  }
}
