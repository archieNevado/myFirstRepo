package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccConfigurationProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for SFCC OCAPI implementation.
 */
@ConfigurationProperties(prefix = "livecontext.sfcc.ocapi")
@Configuration
public class SfccOcapiConfigurationProperties {

  @Value("${livecontext.sfcc.host}")
  private String host = "sandbox.demandware.net";
  private String protocol = "https";

  private int connectionRequestTimeoutMs = 60_000;
  private int connectionTimeoutMs = 60_000;
  private int socketTimeoutMs = 60_000;

  /**
   * URI path variant of {@link SfccConfigurationProperties#SFCC_VENDOR_VERSION}
   */
  private String version = "v18_1";

  /**
   * Base paths for REST API requests
   * <p>
   * Developer Note:
   * On sandbox instances the base paths must be prefixed with '/s/-'
   * Example:
   * livecontext.sfcc.ocapi.dataBasePath=/s/-/dw/data/
   * see: https://documentation.demandware.com/DOC2/topic/com.demandware.dochelp/OCAPI/16.4/usage/UrlSchema.html
   */
  @Value("${livecontext.sfcc.ocapi.data.basePath:/dw/data/}")
  private String dataBasePath = "/dw/data/";

  @Value("${livecontext.sfcc.ocapi.shop.basePath:/dw/shop/}")
  private String shopBasePath = "/dw/shop/";

  @Value("${livecontext.sfcc.ocapi.meta.basePath:/dw/meta/}")
  private String metaBasePath = "/dw/meta/";

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

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getDataBasePath() { return dataBasePath; }

  public void setDataBasePath(String dataBasePath) {
    this.dataBasePath = dataBasePath;
  }

  public String getShopBasePath() {
    return shopBasePath;
  }

  public void setShopBasePath(String shopBasePath) {
    this.shopBasePath = shopBasePath;
  }

  public String getMetaBasePath() {
    return metaBasePath;
  }

  public void setMetaBasePath(String metaBasePath) {
    this.metaBasePath = metaBasePath;
  }

  public int getConnectionRequestTimeoutMs() { return connectionRequestTimeoutMs; }

  public void setConnectionRequestTimeoutMs(int connectionRequestTimeoutMs) { this.connectionRequestTimeoutMs = connectionRequestTimeoutMs; }

  public int getConnectionTimeoutMs() { return connectionTimeoutMs; }

  public void setConnectionTimeoutMs(int connectionTimeoutMs) { this.connectionTimeoutMs = connectionTimeoutMs; }

  public int getSocketTimeoutMs() { return socketTimeoutMs; }

  public void setSocketTimeoutMs(int socketTimeoutMs) { this.socketTimeoutMs = socketTimeoutMs; }
}
