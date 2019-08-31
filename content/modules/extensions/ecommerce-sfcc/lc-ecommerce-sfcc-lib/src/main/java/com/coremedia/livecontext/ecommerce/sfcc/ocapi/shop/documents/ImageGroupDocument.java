package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Document representing an image group
 * containing a list of images for a particular view type
 * and an optional variation value.
 */
public class ImageGroupDocument extends AbstractOCDocument {

  /**
   * The images of the image group.
   */
  @JsonProperty("images")
  private List<ImageDocument> images;

  /**
   * The image variation value.
   */
  @JsonProperty("variation_value")
  private String variationValue;

  /**
   * The image view type.
   */
  @JsonProperty("view_type")
  private String viewType;



  public List<ImageDocument> getImages() {
    return images;
  }

  public void setImages(List<ImageDocument> images) {
    this.images = images;
  }

  public String getVariationValue() {
    return variationValue;
  }

  public void setVariationValue(String variationValue) {
    this.variationValue = variationValue;
  }

  public String getViewType() {
    return viewType;
  }

  public void setViewType(String viewType) {
    this.viewType = viewType;
  }
}
