package com.coremedia.livecontext.ecommerce.hybris;

import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.common.HybrisStoreContextBuilder;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Currency;
import java.util.Locale;

class HybrisTestStoreContextBuilder {

  private HybrisTestStoreContextBuilder() {
  }

  @NonNull
  static StoreContext build(
          @NonNull String storeId,
          @NonNull String storeName,
          @NonNull CatalogId catalogId,
          @NonNull Locale locale,
          @NonNull String currency,
          @NonNull String catalogVersion) {
    return HybrisStoreContextBuilder
            .from("theSiteId")
            .withStoreId(storeId)
            .withStoreName(storeName)
            .withCatalogId(catalogId)
            .withLocale(locale)
            .withCurrency(Currency.getInstance(currency))
            .withCatalogVersion(catalogVersion)
            .build();
  }
}
