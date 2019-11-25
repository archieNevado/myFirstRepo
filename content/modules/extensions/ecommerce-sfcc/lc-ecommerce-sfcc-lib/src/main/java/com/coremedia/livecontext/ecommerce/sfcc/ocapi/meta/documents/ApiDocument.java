package com.coremedia.livecontext.ecommerce.sfcc.ocapi.meta.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * API document.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiDocument {

  @JsonProperty("name")
  private String name;

  @JsonProperty("link")
  private String link;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }
}
