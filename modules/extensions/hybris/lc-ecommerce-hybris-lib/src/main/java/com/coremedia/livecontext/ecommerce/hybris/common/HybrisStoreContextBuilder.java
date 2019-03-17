package com.coremedia.livecontext.ecommerce.hybris.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

@DefaultAnnotation(NonNull.class)
public class HybrisStoreContextBuilder implements StoreContextBuilder {

  private String siteId;
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
  private ZoneId timeZoneId;
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

  public static HybrisStoreContextBuilder from(StoreContextImpl storeContext) {
    return from(storeContext.getSiteId())
            .withStoreId(storeContext.getStoreId())
            .withStoreName(storeContext.getStoreName())
            .withCatalogId(storeContext.getCatalogId().get())
            .withCatalogVersion(storeContext.getCatalogVersion())
            .withCurrency(storeContext.getCurrency())
            .withLocale(storeContext.getLocale())
            .withTimeZoneId(storeContext.getTimeZoneId().orElse(null))
            .withPreviewDate(storeContext.getPreviewDate().orElse(null))
            .withUserSegments(storeContext.getUserSegments().orElse(null));
  }

  @Override
  public HybrisStoreContextBuilder withSiteId(String siteId) {
    this.siteId = siteId;
    return this;
  }

  @Override
  public HybrisStoreContextBuilder withStoreId(String storeId) {
    this.storeId = storeId;
    return this;
  }

  @Override
  public HybrisStoreContextBuilder withStoreName(String storeName) {
    this.storeName = storeName;
    return this;
  }

  @Override
  public HybrisStoreContextBuilder withCatalogId(@Nullable CatalogId catalogId) {
    this.catalogId = catalogId;
    return this;
  }

  @Override
  public HybrisStoreContextBuilder withCatalogAlias(CatalogAlias catalogAlias) {
    // Don't care about catalog alias.
    return this;
  }

  public HybrisStoreContextBuilder withCatalogVersion(String catalogVersion) {
    this.catalogVersion = catalogVersion;
    return this;
  }

  @Override
  public HybrisStoreContextBuilder withCurrency(Currency currency) {
    this.currency = currency;
    return this;
  }

  @Override
  public HybrisStoreContextBuilder withLocale(Locale locale) {
    this.locale = locale;
    return this;
  }

  @Override
  public HybrisStoreContextBuilder withTimeZoneId(@Nullable ZoneId timeZoneId) {
    this.timeZoneId = timeZoneId;
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
  public HybrisStoreContextBuilder withContractIds(List<String> contractIds) {
    // Don't care about contract IDs.
    return this;
  }

  @Override
  public HybrisStoreContextBuilder withContractIdsForPreview(List<String> contractIdsForPreview) {
    // Don't care about contract IDs.
    return this;
  }

  @Override
  public StoreContext build() {
    StoreContextImpl storeContext = StoreContextBuilderImpl.from(siteId)
            .withStoreId(storeId)
            .withStoreName(storeName)
            .withCatalogId(catalogId)
            .withCurrency(currency)
            .withLocale(locale)
            .withTimeZoneId(timeZoneId)
            .withPreviewDate(previewDate)
            .withUserSegments(userSegments)
            .build();

    storeContext.put(StoreContextImpl.CATALOG_VERSION, catalogVersion);

    return storeContext;
  }
}
