package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.LocalizedStringDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

/**
 * Document representing a variation attribute.
 */
public class VariationAttributeDocument extends AbstractOCDocument {

  /**
   * The localized display name of the variation attribute.
   */
  @JsonProperty("name")
  @JsonDeserialize(using = LocalizedStringDeserializer.class)
  private LocalizedProperty<String> name;

  /**
   * The sorted list of variation values.
   * This list can be empty.
   */
  @JsonProperty("values")
  private List<VariationAttributeValueDocument> values;


  public LocalizedProperty<String> getName() {
    return name;
  }

  public void setName(LocalizedProperty<String> name) {
    this.name = name;
  }

  public List<VariationAttributeValueDocument> getValues() {
    return values;
  }

  public void setValues(List<VariationAttributeValueDocument> values) {
    this.values = values;
  }

}
