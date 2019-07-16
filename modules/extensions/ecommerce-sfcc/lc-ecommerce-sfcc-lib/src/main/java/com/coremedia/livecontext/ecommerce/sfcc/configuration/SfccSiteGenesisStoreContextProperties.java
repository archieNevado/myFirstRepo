package com.coremedia.livecontext.ecommerce.sfcc.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "livecontext.sfcc.context.sitegenesis")
public class SfccSiteGenesisStoreContextProperties extends SfccStoreContextProperties {

  private static final String CONFIG_ID = "sitegenesis";

  SfccSiteGenesisStoreContextProperties() {
    // init defaults
    setStoreName("SiteGenesisGlobal");
    setStoreId("SiteGenesisGlobal");
    setCatalogId("storefront-catalog-non-en");
    setCurrency("GBP");
  }

  @Override
  public String getConfigId() {
    return CONFIG_ID;
  }
}
