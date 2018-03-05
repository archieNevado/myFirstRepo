package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.common.SfccStoreContextBuilder;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.LocaleUtils;

import javax.annotation.Nonnull;
import java.util.Currency;
import java.util.Map;

class SfccTestConfigBuilder {

  private SfccTestConfigBuilder() {
  }

  @Nonnull
  static StoreContext build(@Nonnull String siteId, @Nonnull CatalogId catalogId, @Nonnull String storeId,
                            @Nonnull String storeName, @Nonnull String locale, @Nonnull Currency currency) {
    Map<String, String> replacements = ImmutableMap.<String, String>builder()
            .put("catalogId", catalogId.value())
            .put("storeId", storeId)
            .put("storeName", storeName)
            .put("locale", locale)
            .put("currency", currency.getCurrencyCode())
            .build();

    String previewDate = null;
    CatalogAlias catalogAlias = CatalogAlias.of("catalog");

    return SfccStoreContextBuilder
            .from(
                    replacements,
                    siteId,
                    "configId",
                    storeId,
                    storeName,
                    catalogId,
                    catalogAlias,
                    currency,
                    LocaleUtils.toLocale(locale),
                    previewDate
            )
            .build();
  }
}
