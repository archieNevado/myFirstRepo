package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import java.util.Currency;

public class SiteGenesisGlobalTestConfig extends SfccTestConfig {

  @Override
  public StoreContext getStoreContext() {
    return SfccTestConfigBuilder.build(
            "SiteGenesis",
            CatalogId.of("storefront-catalog-non-en"),
            "SiteGenesisGlobal",
            "SiteGenesisGlobal",
            "en_US",
            Currency.getInstance("USD")
    );
  }

  @Override
  public StoreContext getGermanStoreContext() {
    return SfccTestConfigBuilder.build(
            "SiteGenesis",
            CatalogId.of("storefront-catalog-non-en"),
            "SiteGenesisGlobal",
            "SiteGenesisGlobal",
            "de",
            Currency.getInstance("EUR")
    );
  }

}
