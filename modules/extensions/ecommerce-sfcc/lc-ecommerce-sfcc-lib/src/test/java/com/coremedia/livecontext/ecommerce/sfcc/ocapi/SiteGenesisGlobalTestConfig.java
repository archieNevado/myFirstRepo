package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import java.util.Currency;
import java.util.Locale;

import static org.mockito.Mockito.mock;

public class SiteGenesisGlobalTestConfig extends SfccTestConfig {

  private final CommerceConnection commerceConnection;

  public SiteGenesisGlobalTestConfig() {
    this.commerceConnection = mock(CommerceConnection.class);
  }

  @Override
  public StoreContext getStoreContext() {
    return SfccTestConfigBuilder.build(
            commerceConnection,
            "SiteGenesis",
            "SiteGenesisGlobal",
            "SiteGenesisGlobal",
            CatalogId.of("storefront-catalog-non-en"),
            Currency.getInstance("EUR"),
            Locale.UK
    );
  }

  @Override
  public StoreContext getGermanStoreContext() {
    return SfccTestConfigBuilder.build(
            commerceConnection,
            "SiteGenesis",
            "SiteGenesisGlobal",
            "SiteGenesisGlobal",
            CatalogId.of("storefront-catalog-non-en"),
            Currency.getInstance("EUR"),
            Locale.GERMAN
    );
  }
}
