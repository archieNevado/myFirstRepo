package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

@DefaultAnnotation(NonNull.class)
public enum OrderByType {

  ORDER_BY_TYPE_PRICE_ASC("price-low-to-high"),
  ORDER_BY_TYPE_PRICE_DSC("price-high-to-low");

  private final String value;

  OrderByType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
