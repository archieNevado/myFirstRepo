package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.AbstractOCSearchResultDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Category product assignment search result document.
 */
public class CategoryProductAssignmentSearchResultDocument extends AbstractOCSearchResultDocument<CategoryProductAssignmentDocument> {


  @JsonProperty("expand")
  private List<String> expand;

  public List<String> getExpand() {
    return expand;
  }

  public void setExpand(List<String> expand) {
    this.expand = expand;
  }

}
