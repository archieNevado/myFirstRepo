package com.coremedia.ecommerce.studio.rest;

import com.coremedia.cap.content.Content;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Map;

/**
 * Base class for all JSON-representation.
 */
public abstract class CommerceBeanRepresentation extends AbstractCatalogRepresentation {

  private String externalId;
  private String externalTechId;
  private Content content;
  private Map<String, Object> customAttributes;
  private String previewUrl;

  /*
    non-null if content backed commerce object
   */
  public Content getContent() {
    return content;
  }

  public void setContent(Content content) {
    this.content = content;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getExternalTechId() {
    return externalTechId;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getPreviewUrl() {
    return previewUrl;
  }

  public void setExternalTechId(String externalTechId) {
    this.externalTechId = externalTechId;
  }

  public void setPreviewUrl(String url) {
    this.previewUrl = url;
  }

  public Map<String, Object> getCustomAttributes() {
    return customAttributes;
  }

  public void setCustomAttributes(Map<String, Object> customAttributes) {
    this.customAttributes = customAttributes;
  }
}
