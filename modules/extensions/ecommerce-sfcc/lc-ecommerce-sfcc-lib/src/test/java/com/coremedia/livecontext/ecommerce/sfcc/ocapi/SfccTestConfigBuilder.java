package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccStoreContextBuilder;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.LocaleUtils;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Currency;
import java.util.Map;

class SfccTestConfigBuilder {

  private SfccTestConfigBuilder() {
  }

  @NonNull
  static StoreContext build(@NonNull String siteId, @NonNull CatalogId catalogId, @NonNull String storeId,
                            @NonNull String storeName, @NonNull String locale, @NonNull Currency currency) {
    Map<String, String> replacements = ImmutableMap.<String, String>builder()
            .put("catalogId", catalogId.value())
            .put("storeId", storeId)
            .put("storeName", storeName)
            .put("locale", locale)
            .put("currency", currency.getCurrencyCode())
            .build();

    CatalogAlias catalogAlias = CatalogAlias.of("catalog");

    return SfccStoreContextBuilder
            .from(
                    replacements,
                    siteId,
                    storeId,
                    storeName,
                    catalogId,
                    catalogAlias,
                    currency,
                    LocaleUtils.toLocale(locale)
            )
            .build();
  }
}
