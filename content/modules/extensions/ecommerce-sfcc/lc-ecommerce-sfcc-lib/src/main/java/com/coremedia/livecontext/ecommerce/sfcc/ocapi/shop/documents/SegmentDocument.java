package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class SegmentDocument extends AbstractOCDocument {

  @JsonProperty("description")
  private String description;

  @JsonProperty("ruleBased")
  private boolean ruleBased;


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isRuleBased() {
    return ruleBased;
  }

  public void setRuleBased(boolean ruleBased) {
    this.ruleBased = ruleBased;
  }
}
