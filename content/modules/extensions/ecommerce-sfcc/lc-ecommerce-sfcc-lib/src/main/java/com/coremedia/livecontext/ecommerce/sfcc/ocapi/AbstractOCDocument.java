package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractOCDocument {

  protected static final String DATA_API_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'";
  protected static final String SHOP_API_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm'Z'";

  /**
   * Open Commerce API Version.
   */
  @JsonProperty("_v")
  private String ocApiVersion;

  /**
   * Document type.
   */
  @JsonProperty("_type")
  private String ocType;

  /**
   * ID of the document.
   */
  @JsonProperty("id")
  private String id;

  /**
   * Stores all unmapped custom attributes.
   */
  private Map<String, Object> customAttributes = new HashMap<>();

  public String getOcApiVersion() {
    return ocApiVersion;
  }

  public void setOcApiVersion(String ocApiVersion) {
    this.ocApiVersion = ocApiVersion;
  }

  public String getOcType() {
    return ocType;
  }

  public void setOcType(String ocType) {
    this.ocType = ocType;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @JsonAnyGetter
  public Map<String, Object> customAttributes() {
    return customAttributes;
  }

  /**
   * Returns a the value of the provided custom attribute or <code>null</code> if the attribute does not exist.
   *
   * @param name name of the custom attribute
   * @return value of the custom attribute or <code>null</code> if the attribute does not exist
   */
  public Object get(String name) {
    return customAttributes.get(name);
  }

  @JsonAnySetter
  public void set(String name, Object value) {
    customAttributes.put(name, value);
  }
}
