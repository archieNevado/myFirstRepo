package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.AbstractOCSearchResultDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Document representing a product search result.
 */
public class ProductSearchResultDocument extends AbstractOCSearchResultDocument<ProductDocument> {

  @JsonProperty("expand")
  private List<String> expand;

  public List<String> getExpand() {
    return expand;
  }

  public void setExpand(List<String> expand) {
    this.expand = expand;
  }
}
