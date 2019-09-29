package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Document representing a custom object that contains all defined custom attributes for its object type.
 */
public class CustomObjectDocument extends AbstractOCDocument {

  /**
   * The name of the key property for the custom object.
   * This is ignored in input documents.
   */
  @JsonProperty("key_property")
  private String key;

  /**
   * The id of the custom object when the type of the key is Integer.
   * This is ignored in input documents.
   */
  @JsonProperty("key_value_integer")
  private int integerValue;

  /**
   * The id of the custom object when the type of the key is String.
   * This is ignored in input documents.
   */
  @JsonProperty("key_value_string")
  private String stringValue;

  /**
   * The id of the object type.
   * This is ignored in input documents.
   */
  @JsonProperty("object_type")
  private String objectType;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public int getIntegerValue() {
    return integerValue;
  }

  public void setIntegerValue(int integerValue) {
    this.integerValue = integerValue;
  }

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }
}
