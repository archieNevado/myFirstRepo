package com.coremedia.livecontext.ecommerce.ibm.catalog;

/**
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated
enum SearchType {
  SEARCH_TYPE_PRODUCTS("2"),
  SEARCH_TYPE_PRODUCT_VARIANTS("102");
  private final String value;

  SearchType(final String searchType) {
    this.value = searchType;
  }

  public String getValue() {
    return value;
  }
}
