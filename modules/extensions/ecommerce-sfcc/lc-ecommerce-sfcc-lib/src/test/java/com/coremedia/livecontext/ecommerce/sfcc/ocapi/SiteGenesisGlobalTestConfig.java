package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import java.util.Currency;
import java.util.Locale;

public class SiteGenesisGlobalTestConfig extends SfccTestConfig {

  @Override
  public StoreContext getStoreContext() {
    return SfccTestConfigBuilder.build(
            "SiteGenesis",
            "SiteGenesisGlobal", "SiteGenesisGlobal", CatalogId.of("storefront-catalog-non-en"),
            Currency.getInstance("EUR"), Locale.UK
    );
  }

  @Override
  public StoreContext getGermanStoreContext() {
    return SfccTestConfigBuilder.build(
            "SiteGenesis",
            "SiteGenesisGlobal", "SiteGenesisGlobal", CatalogId.of("storefront-catalog-non-en"),
            Currency.getInstance("EUR"), Locale.GERMAN
    );
  }
}
