package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Document representing a product image.
 */
public class ImageDocument extends AbstractOCDocument {

  /**
   * The localized alternative text of the image.
   */
  @JsonProperty("alt")
  private String altText;

  /**
   * The URL of the actual image.
   */
  @JsonProperty("link")
  private String link;

  /**
   * The localized title of the image.
   */
  @JsonProperty("title")
  private String title;



  public String getAltText() {
    return altText;
  }

  public void setAltText(String altText) {
    this.altText = altText;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
