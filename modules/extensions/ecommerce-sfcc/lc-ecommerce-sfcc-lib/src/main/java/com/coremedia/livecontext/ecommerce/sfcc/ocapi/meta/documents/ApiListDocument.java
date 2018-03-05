package com.coremedia.livecontext.ecommerce.sfcc.ocapi.meta.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * API list.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiListDocument {

  @JsonProperty("apis")
  List<ApiDocument> apis;

  public List<ApiDocument> getApis() {
    return apis;
  }

  public void setApis(List<ApiDocument> apis) {
    this.apis = apis;
  }

}
