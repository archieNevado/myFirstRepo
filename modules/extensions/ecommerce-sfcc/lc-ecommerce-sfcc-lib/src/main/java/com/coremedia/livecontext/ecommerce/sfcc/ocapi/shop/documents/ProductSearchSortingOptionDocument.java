package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductSearchSortingOptionDocument extends AbstractOCDocument {

  /**
   * The id of the sorting option.
   */
  @JsonProperty("id")
  private String id;

  /**
   * The localized label of the sorting option.
   */
  @JsonProperty("label")
  private String label;


  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
}
