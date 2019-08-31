package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccStoreContextBuilder;
import com.google.common.collect.ImmutableMap;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Currency;
import java.util.Locale;
import java.util.Map;

class SfccTestConfigBuilder {

  private SfccTestConfigBuilder() {
  }

  @NonNull
  static StoreContext build(@NonNull CommerceConnection commerceConnection, @NonNull String siteId,
                            @NonNull String storeId, @NonNull String storeName, @NonNull CatalogId catalogId,
                            @NonNull Currency currency, @NonNull Locale locale) {
    Map<String, String> replacements = ImmutableMap.<String, String>builder()
            .put("catalogId", catalogId.value())
            .put("storeId", storeId)
            .put("storeName", storeName)
            .put("locale", locale.toString())
            .put("currency", currency.getCurrencyCode())
            .build();

    CatalogAlias catalogAlias = CatalogAlias.of("catalog");

    return SfccStoreContextBuilder
            .from(
                    commerceConnection,
                    replacements,
                    siteId,
                    storeId,
                    storeName,
                    catalogId,
                    catalogAlias,
                    currency,
                    locale
            )
            .build();
  }
}
