package com.coremedia.livecontext.ecommerce.sfcc.common;

import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import com.google.common.collect.ImmutableMap;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@DefaultAnnotation(NonNull.class)
public class SfccStoreContextBuilder implements StoreContextBuilder {

  private final CommerceConnection connection;
  private final ImmutableMap<String, String> replacements;
  private String siteId;
  private String storeId;
  private String storeName;
  @Nullable
  private CatalogId catalogId;
  private CatalogAlias catalogAlias;
  private Currency currency;
  private Locale locale;

  @Nullable
  private ZonedDateTime previewDate;
  @Nullable
  private String userSegments;

  @SuppressWarnings({"MethodWithTooManyParameters", "squid:S00107"}) // "Methods should not have too many parameters"
  private SfccStoreContextBuilder(
          CommerceConnection connection,
          Map<String, String> replacements,
          String siteId,
          String storeId,
          String storeName,
          CatalogId catalogId,
          CatalogAlias catalogAlias,
          Currency currency,
          Locale locale) {
    this.connection = connection;
    this.replacements = ImmutableMap.copyOf(replacements);
    this.siteId = siteId;
    this.storeId = storeId;
    this.storeName = storeName;
    this.catalogId = catalogId;
    this.catalogAlias = catalogAlias;
    this.currency = currency;
    this.locale = locale;
  }

  @SuppressWarnings({"MethodWithTooManyParameters", "squid:S00107"}) // "Methods should not have too many parameters"
  public static SfccStoreContextBuilder from(
          CommerceConnection connection,
          Map<String, String> replacements,
          String siteId,
          String storeId,
          String storeName,
          CatalogId catalogId,
          CatalogAlias catalogAlias,
          Currency currency,
          Locale locale) {
    return new SfccStoreContextBuilder(
            connection,
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

  public static SfccStoreContextBuilder from(SfccStoreContext storeContext) {
    return from(
            storeContext.getConnection(),
            storeContext.getReplacements(),
            storeContext.getSiteId(),
            storeContext.getStoreId(),
            storeContext.getStoreName(),
            storeContext.getCatalogId().get(),
            storeContext.getCatalogAlias(),
            storeContext.getCurrency(),
            storeContext.getLocale()
    )
            .withPreviewDate(storeContext.getPreviewDate().orElse(null))
            .withUserSegments(storeContext.getUserSegments().orElse(null));
  }

  @Override
  public SfccStoreContextBuilder withSiteId(String siteId) {
    this.siteId = siteId;
    return this;
  }

  @Override
  public SfccStoreContextBuilder withStoreId(String storeId) {
    this.storeId = storeId;
    return this;
  }

  @Override
  public SfccStoreContextBuilder withStoreName(String storeName) {
    this.storeName = storeName;
    return this;
  }

  @Override
  public SfccStoreContextBuilder withCatalogId(@Nullable CatalogId catalogId) {
    this.catalogId = catalogId;
    return this;
  }

  @Override
  public SfccStoreContextBuilder withCatalogAlias(CatalogAlias catalogAlias) {
    this.catalogAlias = catalogAlias;
    return this;
  }

  @Override
  public SfccStoreContextBuilder withCurrency(Currency currency) {
    this.currency = currency;
    return this;
  }

  @Override
  public SfccStoreContextBuilder withLocale(Locale locale) {
    this.locale = locale;
    return this;
  }

  @Override
  public SfccStoreContextBuilder withTimeZoneId(@Nullable ZoneId timeZoneId) {
    // currently not used
    return this;
  }

  @Override
  public SfccStoreContextBuilder withPreviewDate(@Nullable ZonedDateTime previewDate) {
    this.previewDate = previewDate;
    return this;
  }

  @Override
  public SfccStoreContextBuilder withWorkspaceId(@Nullable WorkspaceId workspaceId) {
    // Don't care about the workspace ID.
    return this;
  }

  @Override
  public SfccStoreContextBuilder withUserSegments(@Nullable String userSegments) {
    this.userSegments = userSegments;
    return this;
  }

  @Override
  public SfccStoreContextBuilder withContractIds(List<String> contractIds) {
    // Don't care about contract IDs.
    return this;
  }

  @Override
  public SfccStoreContextBuilder withContractIdsForPreview(List<String> contractIdsForPreview) {
    // Don't care about contract IDs.
    return this;
  }

  @Override
  public SfccStoreContext build() {
    return new SfccStoreContext(
            connection,
            replacements,
            siteId,
            storeId,
            storeName,
            catalogId,
            catalogAlias,
            currency,
            locale,
            previewDate,
            userSegments
    );
  }
}
