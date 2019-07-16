package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Document representing a custom object
 * that contains all defined custom attributes for its object type.
 */
public class CustomObjectDocument extends AbstractOCDocument{

  /**
   * The name of the key property for the custom object.
   */
  @JsonProperty("key_property")
  private String keyProperty;

  /**
   * The id of the custom object when the type of the key is Integer.
   */
  @JsonProperty("key_value_integer")
  private String keyValueInteger;

  /**
   * The id of the custom object when the type of the key is String.
   */
  @JsonProperty("key_value_string")
  private String keyValueString;

  /**
   * The id of the object type.
   */
  @JsonProperty("object_type")
  private String objectType;


  public String getKeyProperty() {
    return keyProperty;
  }

  public void setKeyProperty(String keyProperty) {
    this.keyProperty = keyProperty;
  }

  public String getKeyValueInteger() {
    return keyValueInteger;
  }

  public void setKeyValueInteger(String keyValueInteger) {
    this.keyValueInteger = keyValueInteger;
  }

  public String getKeyValueString() {
    return keyValueString;
  }

  public void setKeyValueString(String keyValueString) {
    this.keyValueString = keyValueString;
  }

  public String getObjectType() {
    return objectType;
  }

  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }
}
