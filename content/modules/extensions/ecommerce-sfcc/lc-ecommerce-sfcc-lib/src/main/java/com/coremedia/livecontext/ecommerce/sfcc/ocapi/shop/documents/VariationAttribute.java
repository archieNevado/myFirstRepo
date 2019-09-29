package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class VariationAttribute extends AbstractOCDocument {

  @JsonProperty("name")
  private String name;

  @JsonProperty("variation_attributes")
  private List<VariationAttributeValue> variationAttributes = new ArrayList<>();
  private Object values;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<VariationAttributeValue> getVariationAttributes() {
    return variationAttributes;
  }

  public void setVariationAttributes(List<VariationAttributeValue> variationAttributes) {
    this.variationAttributes = variationAttributes;
  }

  public Object getValues() {
    Map<String,String> values = new HashMap<>();
    for (VariationAttributeValue variationAttribute : variationAttributes) {
      values.put(variationAttribute.getName(), variationAttribute.getValue());
    }
    return values;
  }
}
