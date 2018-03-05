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

  private ImmutableMap<String, String> replacements;
  private String siteId;
  private String configId;
  private String storeId;
  private String storeName;
  private CatalogId catalogId;
  private CatalogAlias catalogAlias;
  private Currency currency;
  private Locale locale;
  private String previewDate;

  @Nonnull
  @SuppressWarnings({"MethodWithTooManyParameters", "squid:S00107"}) // "Methods should not have too many parameters"
  public static SfccStoreContextBuilder from(
          @Nonnull Map<String, String> replacements,
          @Nonnull String siteId,
          @Nonnull String configId,
          @Nonnull String storeId,
          @Nonnull String storeName,
          @Nonnull CatalogId catalogId,
          @Nonnull CatalogAlias catalogAlias,
          @Nonnull Currency currency,
          @Nonnull Locale locale,
          @Nullable String previewDate) {
    return new SfccStoreContextBuilder()
            .withReplacements(replacements)
            .withSiteId(siteId)
            .withConfigId(configId)
            .withStoreId(storeId)
            .withStoreName(storeName)
            .withCatalog(catalogId, catalogAlias)
            .withCurrency(currency)
            .withLocale(locale)
            .withPreviewDate(previewDate);
  }

  @Nonnull
  @Override
  public SfccStoreContextBuilder from(@Nonnull StoreContext storeContext) {
    return from(
            storeContext.getReplacements(),
            storeContext.getSiteId(),
            storeContext.getConfigId(),
            storeContext.getStoreId(),
            storeContext.getStoreName(),
            CatalogId.of(storeContext.getCatalogId()),
            storeContext.getCatalogAlias(),
            storeContext.getCurrency(),
            storeContext.getLocale(),
            storeContext.getPreviewDate()
    );
  }

  @Nonnull
  public SfccStoreContextBuilder withReplacements(@Nonnull Map<String, String> replacements) {
    this.replacements = ImmutableMap.copyOf(replacements);
    return this;
  }

  @Nonnull
  public SfccStoreContextBuilder withSiteId(@Nonnull String siteId) {
    this.siteId = siteId;
    return this;
  }

  @Nonnull
  public SfccStoreContextBuilder withConfigId(@Nonnull String configId) {
    this.configId = configId;
    return this;
  }

  @Nonnull
  public SfccStoreContextBuilder withStoreId(@Nonnull String storeId) {
    this.storeId = storeId;
    return this;
  }

  @Nonnull
  public SfccStoreContextBuilder withStoreName(@Nonnull String storeName) {
    this.storeName = storeName;
    return this;
  }

  @Nonnull
  public SfccStoreContextBuilder withCatalog(@Nonnull CatalogId catalogId, @Nonnull CatalogAlias catalogAlias) {
    this.catalogId = catalogId;
    this.catalogAlias = catalogAlias;
    return this;
  }

  @Nonnull
  public SfccStoreContextBuilder withCurrency(@Nonnull Currency currency) {
    this.currency = currency;
    return this;
  }

  @Nonnull
  public SfccStoreContextBuilder withLocale(@Nonnull Locale locale) {
    this.locale = locale;
    return this;
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
            configId,
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
