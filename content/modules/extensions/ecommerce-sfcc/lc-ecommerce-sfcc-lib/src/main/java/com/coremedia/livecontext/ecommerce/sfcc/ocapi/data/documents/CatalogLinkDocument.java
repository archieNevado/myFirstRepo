package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Catalog link document.
 */
public class CatalogLinkDocument extends AbstractOCDocument {

  @JsonProperty("link")
  private String link;

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }
}
