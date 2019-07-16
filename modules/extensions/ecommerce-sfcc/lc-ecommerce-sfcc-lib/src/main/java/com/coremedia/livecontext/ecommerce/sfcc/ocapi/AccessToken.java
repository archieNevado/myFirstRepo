package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO holding an access token.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {

  @JsonProperty("access_token")
  private String token;

  @JsonProperty("scope")
  private String scope;

  @JsonProperty("token_type")
  private String type;

  @JsonProperty("expires_in")
  private long expiresIn;

  private long createdAt;


  public AccessToken() {
    createdAt = System.currentTimeMillis();
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public long getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(long expiresIn) {
    this.expiresIn = expiresIn;
  }

  /**
   * Checks whether or not the token is expired.
   *
   * @return <code>true</code> if the token is expired, <code>false</code> otherwise
   */
  public boolean isExpired() {
    return System.currentTimeMillis() - createdAt > (expiresIn - 10) * 1000; // expire 10 seconds early
  }

  /**
   * Returns the HTTP-Header token value that can be used in an Authorization header.
   * @return
   */
  public String toHttpHeaderValue() {
    return type + " " + token;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
            "token=" + token +
            ", type=" + type +
            ", scope=" + scope +
            ", expired=" + isExpired() +
            "}";
  }
}
