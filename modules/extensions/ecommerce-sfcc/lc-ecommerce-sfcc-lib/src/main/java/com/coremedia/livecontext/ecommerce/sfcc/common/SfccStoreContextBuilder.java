package com.coremedia.livecontext.ecommerce.sfcc.common;

import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

public class SfccStoreContextBuilder implements StoreContextBuilder {

  private final ImmutableMap<String, String> replacements;
  private final String siteId;
  private final String storeId;
  private final String storeName;
  private final CatalogId catalogId;
  private final CatalogAlias catalogAlias;
  private final Currency currency;
  private final Locale locale;
  private String previewDate;

  @SuppressWarnings({"MethodWithTooManyParameters", "squid:S00107"}) // "Methods should not have too many parameters"
  private SfccStoreContextBuilder(
          @Nonnull Map<String, String> replacements,
          @Nonnull String siteId,
          @Nonnull String storeId,
          @Nonnull String storeName,
          @Nonnull CatalogId catalogId,
          @Nonnull CatalogAlias catalogAlias,
          @Nonnull Currency currency,
          @Nonnull Locale locale) {
    this.replacements = ImmutableMap.copyOf(replacements);
    this.siteId = siteId;
    this.storeId = storeId;
    this.storeName = storeName;
    this.catalogId = catalogId;
    this.catalogAlias = catalogAlias;
    this.currency = currency;
    this.locale = locale;
  }

  @Nonnull
  @SuppressWarnings({"MethodWithTooManyParameters", "squid:S00107"}) // "Methods should not have too many parameters"
  public static SfccStoreContextBuilder from(
          @Nonnull Map<String, String> replacements,
          @Nonnull String siteId,
          @Nonnull String storeId,
          @Nonnull String storeName,
          @Nonnull CatalogId catalogId,
          @Nonnull CatalogAlias catalogAlias,
          @Nonnull Currency currency,
          @Nonnull Locale locale) {
    return new SfccStoreContextBuilder(
            replacements,
            siteId,
            storeId,
            storeName,
            catalogId,
            catalogAlias,
            currency,
            locale
    );
  }

  @Nonnull
  public static SfccStoreContextBuilder from(@Nonnull StoreContext storeContext) {
    return from(
            storeContext.getReplacements(),
            storeContext.getSiteId(),
            storeContext.getStoreId(),
            storeContext.getStoreName(),
            CatalogId.of(storeContext.getCatalogId()),
            storeContext.getCatalogAlias(),
            storeContext.getCurrency(),
            storeContext.getLocale()
    )
            .withPreviewDate(storeContext.getPreviewDate());
  }

  @Nonnull
  @Override
  public SfccStoreContextBuilder withPreviewDate(@Nullable String previewDate) {
    this.previewDate = previewDate;
    return this;
  }

  @Nonnull
  @Override
  public SfccStoreContextBuilder withWorkspaceId(String workspaceId) {
    // Don't care about the workspace ID.
    return this;
  }

  @Nonnull
  @Override
  public SfccStoreContext build() {
    return new SfccStoreContext(
            replacements,
            siteId,
            storeId,
            storeName,
            catalogId,
            catalogAlias,
            currency,
            locale,
            previewDate
    );
  }
}
