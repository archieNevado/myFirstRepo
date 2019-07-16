package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class VariationAttributeValue extends VariationAttribute {

  @JsonProperty("value")
  private String value;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
