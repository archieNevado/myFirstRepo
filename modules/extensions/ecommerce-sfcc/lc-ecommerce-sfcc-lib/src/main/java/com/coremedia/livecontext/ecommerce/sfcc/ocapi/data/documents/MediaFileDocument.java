package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.LocalizedStringDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @since 17.1
 */
public class MediaFileDocument extends AbstractOCDocument {

  /**
   * The absolute URL with request protocol.
   */
  @JsonProperty("abs_url")
  private String absUrl;

  /**
   * The alternative image text.
   */
  @JsonProperty("alt")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> alt;

  /**
   * The DIS base URL only for product images.
   */
  @JsonProperty("dis_base_url")
  private String disBaseUrl;

  /**
   * The raw media file path
   */
  @JsonProperty("path")
  private String path;

  /**
   * The image title.
   */
  @JsonProperty("title")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> title;


  public String getAbsUrl() {
    return absUrl;
  }

  public void setAbsUrl(String absUrl) {
    this.absUrl = absUrl;
  }

  public LocalizedProperty<String> getAlt() {
    return alt;
  }

  public void setAlt(LocalizedProperty<String> alt) {
    this.alt = alt;
  }

  public String getDisBaseUrl() {
    return disBaseUrl;
  }

  public void setDisBaseUrl(String disBaseUrl) {
    this.disBaseUrl = disBaseUrl;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public LocalizedProperty<String> getTitle() {
    return title;
  }

  public void setTitle(LocalizedProperty<String> title) {
    this.title = title;
  }
}
