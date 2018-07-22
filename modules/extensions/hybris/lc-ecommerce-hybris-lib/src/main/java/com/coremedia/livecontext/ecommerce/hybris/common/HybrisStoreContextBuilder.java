package com.coremedia.livecontext.ecommerce.hybris.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CATALOG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CATALOG_VERSION;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CURRENCY;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.LOCALE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_NAME;

@DefaultAnnotation(NonNull.class)
public class HybrisStoreContextBuilder implements StoreContextBuilder {

  private final String siteId;

  @Nullable
  private String storeId;
  @Nullable
  private String storeName;
  @Nullable
  private CatalogId catalogId;
  @Nullable
  private String catalogVersion;
  @Nullable
  private Currency currency;
  @Nullable
  private Locale locale;
  @Nullable
  private ZonedDateTime previewDate;
  @Nullable
  private String userSegments;

  private HybrisStoreContextBuilder(String siteId) {
    this.siteId = siteId;
  }

  public static HybrisStoreContextBuilder from(String siteId) {
    return new HybrisStoreContextBuilder(siteId);
  }

  public static HybrisStoreContextBuilder from(StoreContext storeContext) {
    return from(storeContext.getSiteId())
            .withStoreId(storeContext.getStoreId())
            .withStoreName(storeContext.getStoreName())
            .withCatalogId(CatalogId.of(storeContext.getCatalogId()))
            .withCatalogVersion(storeContext.getCatalogVersion())
            .withCurrency(storeContext.getCurrency())
            .withLocale(storeContext.getLocale())
            .withPreviewDate(storeContext.getPreviewDate().orElse(null))
            .withUserSegments(storeContext.getUserSegments());
  }

  public HybrisStoreContextBuilder withStoreId(String storeId) {
    this.storeId = storeId;
    return this;
  }

  public HybrisStoreContextBuilder withStoreName(String storeName) {
    this.storeName = storeName;
    return this;
  }

  public HybrisStoreContextBuilder withCatalogId(CatalogId catalogId) {
    this.catalogId = catalogId;
    return this;
  }

  public HybrisStoreContextBuilder withCatalogVersion(String catalogVersion) {
    this.catalogVersion = catalogVersion;
    return this;
  }

  public HybrisStoreContextBuilder withCurrency(Currency currency) {
    this.currency = currency;
    return this;
  }

  public HybrisStoreContextBuilder withLocale(Locale locale) {
    this.locale = locale;
    return this;
  }

  @Override
  public HybrisStoreContextBuilder withPreviewDate(@Nullable ZonedDateTime previewDate) {
    this.previewDate = previewDate;
    return this;
  }

  @Override
  public HybrisStoreContextBuilder withWorkspaceId(@Nullable WorkspaceId workspaceId) {
    // Don't care about the workspace ID.
    return this;
  }

  @Override
  public HybrisStoreContextBuilder withUserSegments(@Nullable String userSegments) {
    this.userSegments = userSegments;
    return this;
  }

  @Override
  public StoreContextBuilder withContractIds(List<String> contractIds) {
    // Don't care about contract IDs.
    return this;
  }

  @Override
  public StoreContextBuilder withContractIdsForPreview(List<String> contractIds) {
    // Don't care about contract IDs.
    return this;
  }

  @Override
  public StoreContext build() {
    StoreContext storeContext = StoreContextImpl.builder(siteId)
            .withPreviewDate(previewDate)
            .withUserSegments(userSegments)
            .build();

    storeContext.put(STORE_ID, storeId);
    storeContext.put(STORE_NAME, storeName);
    storeContext.put(CATALOG_ID, catalogId != null ? catalogId.value() : null);
    storeContext.put(CATALOG_VERSION, catalogVersion);
    storeContext.put(CURRENCY, currency);
    storeContext.put(LOCALE, locale);

    return storeContext;
  }
}
