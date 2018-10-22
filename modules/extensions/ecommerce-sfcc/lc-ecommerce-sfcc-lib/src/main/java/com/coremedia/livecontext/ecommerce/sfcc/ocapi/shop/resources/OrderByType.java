package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources;

public enum OrderByType {

  ORDER_BY_TYPE_PRICE_ASC("price-low-to-high"),
  ORDER_BY_TYPE_PRICE_DSC("price-high-to-low");

  private final String value;

  OrderByType(final String orderByType) {
    this.value = orderByType;
  }

  public String getValue() {
    return value;
  }
}
