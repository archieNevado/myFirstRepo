package com.coremedia.livecontext.ecommerce.hybris.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Currency;
import java.util.Locale;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CATALOG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CATALOG_VERSION;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CURRENCY;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.LOCALE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.PREVIEW_DATE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_NAME;

public class HybrisStoreContextBuilder implements StoreContextBuilder {

  private final String siteId;

  private String storeId;
  private String storeName;
  private CatalogId catalogId;
  private String catalogVersion;
  private Currency currency;
  private Locale locale;
  private String previewDate;

  private HybrisStoreContextBuilder(@Nonnull String siteId) {
    this.siteId = siteId;
  }

  @Nonnull
  public static HybrisStoreContextBuilder from(@Nonnull String siteId) {
    return new HybrisStoreContextBuilder(siteId);
  }

  @Nonnull
  public static HybrisStoreContextBuilder from(@Nonnull StoreContext storeContext) {
    return from(storeContext.getSiteId())
            .withStoreId(storeContext.getStoreId())
            .withStoreName(storeContext.getStoreName())
            .withCatalogId(CatalogId.of(storeContext.getCatalogId()))
            .withCatalogVersion(storeContext.getCatalogVersion())
            .withCurrency(storeContext.getCurrency())
            .withLocale(storeContext.getLocale())
            .withPreviewDate(storeContext.getPreviewDate());
  }

  @Nonnull
  public HybrisStoreContextBuilder withStoreId(@Nonnull String storeId) {
    this.storeId = storeId;
    return this;
  }

  @Nonnull
  public HybrisStoreContextBuilder withStoreName(@Nonnull String storeName) {
    this.storeName = storeName;
    return this;
  }

  @Nonnull
  public HybrisStoreContextBuilder withCatalogId(@Nonnull CatalogId catalogId) {
    this.catalogId = catalogId;
    return this;
  }

  @Nonnull
  public HybrisStoreContextBuilder withCatalogVersion(@Nonnull String catalogVersion) {
    this.catalogVersion = catalogVersion;
    return this;
  }

  @Nonnull
  public HybrisStoreContextBuilder withCurrency(@Nonnull Currency currency) {
    this.currency = currency;
    return this;
  }

  @Nonnull
  public HybrisStoreContextBuilder withLocale(@Nonnull Locale locale) {
    this.locale = locale;
    return this;
  }

  @Nonnull
  @Override
  public HybrisStoreContextBuilder withPreviewDate(@Nullable String previewDate) {
    this.previewDate = previewDate;
    return this;
  }

  @Nonnull
  @Override
  public HybrisStoreContextBuilder withWorkspaceId(String workspaceId) {
    // Don't care about the workspace ID.
    return this;
  }

  @Nonnull
  @Override
  public StoreContext build() {
    StoreContext storeContext = StoreContextImpl.newStoreContext();

    storeContext.setSiteId(siteId);
    storeContext.put(STORE_ID, storeId);
    storeContext.put(STORE_NAME, storeName);
    storeContext.put(CATALOG_ID, catalogId != null ? catalogId.value() : null);
    storeContext.put(CATALOG_VERSION, catalogVersion);
    storeContext.put(CURRENCY, currency);
    storeContext.put(LOCALE, locale);
    storeContext.put(PREVIEW_DATE, previewDate);

    return storeContext;
  }
}
