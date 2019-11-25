package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.LocalizedStringDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Document representing a variation attribute value.
 */
public class VariationAttributeValueDocument extends AbstractOCDocument {

  /**
   * The localized description of the variation value.
   */
  @JsonProperty("description")
  private LocalizedProperty<String> description;

  /**
   * The first product image for the configured viewtype and this variation value.
   */
  @JsonProperty("image")
  private MediaFileDocument image;

  /**
   * The first product image for the configured viewtype and this variation value. (typically the swatch image)
   */
  @JsonProperty("image_swatch")
  private MediaFileDocument imageSwatch;

  /**
   * The localized display name of the variation value.
   */
  @JsonProperty("name")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> name;

  /**
   * A flag indicating whether at least one variant with this variation attribute value is available to sell.
   */
  @JsonProperty("orderable")
  private Boolean orderable;

  /**
   * The actual variation value.
   */
  @JsonProperty("value")
  private String value;


  public LocalizedProperty<String> getDescription() {
    return description;
  }

  public void setDescription(LocalizedProperty<String> description) {
    this.description = description;
  }

  public MediaFileDocument getImage() {
    return image;
  }

  public void setImage(MediaFileDocument image) {
    this.image = image;
  }

  public MediaFileDocument getImageSwatch() {
    return imageSwatch;
  }

  public void setImageSwatch(MediaFileDocument imageSwatch) {
    this.imageSwatch = imageSwatch;
  }

  public LocalizedProperty<String> getName() {
    return name;
  }

  public void setName(LocalizedProperty<String> name) {
    this.name = name;
  }

  public Boolean getOrderable() {
    return orderable;
  }

  public void setOrderable(Boolean orderable) {
    this.orderable = orderable;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
