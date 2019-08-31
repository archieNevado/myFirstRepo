package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for SFCC OCAPI implementation.
 */
@ConfigurationProperties(prefix = "livecontext.sfcc.oauth")
@Configuration
public class SfccOAuthConfigurationProperties {

  private static final String DEFAULT_OAUTH_TOKEN_PATH = "/dw/oauth2/access_token";

  private String host = "account.demandware.com";
  private String protocol = "https";
  private String path = DEFAULT_OAUTH_TOKEN_PATH;
  private String clientId;
  private String clientPassword;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientPassword() {
    return clientPassword;
  }

  public void setClientPassword(String clientPassword) {
    this.clientPassword = clientPassword;
  }
}
