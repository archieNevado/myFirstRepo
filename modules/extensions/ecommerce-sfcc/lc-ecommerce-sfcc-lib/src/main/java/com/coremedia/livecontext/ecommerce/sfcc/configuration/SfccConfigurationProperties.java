package com.coremedia.livecontext.ecommerce.sfcc.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

/**
 * Configuration properties for SFCC implementation
 */
@ConfigurationProperties(prefix = "livecontext.sfcc")
public class SfccConfigurationProperties {

  public static final String SFCC_VENDOR_VERSION = "18.1";

  private String host = "shop.demandware.net";
  private String vendorVersion = SFCC_VENDOR_VERSION;

  @Value("${livecontext.sfcc.default.locale:en_US}")
  private Locale defaultLocale = Locale.US;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getVendorVersion() {
    return vendorVersion;
  }

  public void setVendorVersion(String vendorVersion) {
    this.vendorVersion = vendorVersion;
  }

  /**
   * The default locale for the commerce system. This may be used as fallback if certain information are
   * not set on localized commerce objects. If, for example, the german seo segment for a product is not
   * given, the CAE tries to read the seo segment for the default locale.
   */
  public Locale getDefaultLocale() {
    return defaultLocale;
  }

  public void setDefaultLocale(Locale defaultLocale) {
    this.defaultLocale = defaultLocale;
  }

}
