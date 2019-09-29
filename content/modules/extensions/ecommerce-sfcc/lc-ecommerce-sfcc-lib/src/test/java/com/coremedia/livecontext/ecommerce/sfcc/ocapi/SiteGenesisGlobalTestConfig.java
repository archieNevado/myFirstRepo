package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Currency;
import java.util.Locale;

public class SiteGenesisGlobalTestConfig extends SfccTestConfig {

  @Override
  public StoreContext getStoreContext(@NonNull CommerceConnection connection) {
    return SfccTestConfigBuilder.build(
            connection,
            "SiteGenesis",
            "SiteGenesisGlobal",
            "SiteGenesisGlobal",
            CatalogId.of("storefront-catalog-non-en"),
            Currency.getInstance("EUR"),
            Locale.UK
    );
  }

  @Override
  public StoreContext getGermanStoreContext(@NonNull CommerceConnection connection) {
    return SfccTestConfigBuilder.build(
            connection,
            "SiteGenesis",
            "SiteGenesisGlobal",
            "SiteGenesisGlobal",
            CatalogId.of("storefront-catalog-non-en"),
            Currency.getInstance("EUR"),
            Locale.GERMAN
    );
  }
}
