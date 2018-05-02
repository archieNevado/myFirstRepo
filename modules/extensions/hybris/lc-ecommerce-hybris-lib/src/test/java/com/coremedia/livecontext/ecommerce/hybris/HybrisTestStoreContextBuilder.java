package com.coremedia.livecontext.ecommerce.hybris;

import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.common.HybrisStoreContextBuilder;

import javax.annotation.Nonnull;
import java.util.Currency;
import java.util.Locale;

class HybrisTestStoreContextBuilder {

  private HybrisTestStoreContextBuilder() {
  }

  @Nonnull
  static StoreContext build(
          @Nonnull String storeId,
          @Nonnull String storeName,
          @Nonnull CatalogId catalogId,
          @Nonnull Locale locale,
          @Nonnull String currency,
          @Nonnull String catalogVersion) {
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
