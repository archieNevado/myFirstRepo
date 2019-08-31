package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class CustomerToken extends AbstractOCDocument {

  @JsonProperty("auth_type")
  private String authType;

  @JsonProperty("customer_id")
  private String customerId;

  @JsonProperty("preferred_locale")
  private String preferredLocale;

  private String authToken;

  public String getAuthType() {
    return authType;
  }

  public void setAuthType(String authType) {
    this.authType = authType;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getPreferredLocale() {
    return preferredLocale;
  }

  public void setPreferredLocale(String preferredLocale) {
    this.preferredLocale = preferredLocale;
  }

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }
}
