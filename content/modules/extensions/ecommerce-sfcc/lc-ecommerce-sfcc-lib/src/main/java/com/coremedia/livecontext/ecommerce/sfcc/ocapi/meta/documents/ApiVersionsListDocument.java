package com.coremedia.livecontext.ecommerce.sfcc.ocapi.meta.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * List of API versions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiVersionsListDocument {

  @JsonProperty("versions")
  List<ApiVersionDocument> versions;

  public List<ApiVersionDocument> getVersions() {
    return versions;
  }

  public void setVersions(List<ApiVersionDocument> versions) {
    this.versions = versions;
  }

}
