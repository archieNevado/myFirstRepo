package com.coremedia.livecontext.ecommerce.sfcc.common;

import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import com.google.common.collect.ImmutableMap;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@DefaultAnnotation(NonNull.class)
public class SfccStoreContextBuilder implements StoreContextBuilder {

  private final ImmutableMap<String, String> replacements;
  private final String siteId;
  private final String storeId;
  private final String storeName;
  private final CatalogId catalogId;
  private final CatalogAlias catalogAlias;
  private final Currency currency;
  private final Locale locale;

  @Nullable
  private ZonedDateTime previewDate;
  @Nullable
  private String userSegments;

  @SuppressWarnings({"MethodWithTooManyParameters", "squid:S00107"}) // "Methods should not have too many parameters"
  private SfccStoreContextBuilder(
          Map<String, String> replacements,
          String siteId,
          String storeId,
          String storeName,
          CatalogId catalogId,
          CatalogAlias catalogAlias,
          Currency currency,
          Locale locale) {
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
          Map<String, String> replacements,
          String siteId,
          String storeId,
          String storeName,
          CatalogId catalogId,
          CatalogAlias catalogAlias,
          Currency currency,
          Locale locale) {
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

  public static SfccStoreContextBuilder from(StoreContext storeContext) {
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
            .withPreviewDate(storeContext.getPreviewDate().orElse(null))
            .withUserSegments(storeContext.getUserSegments());
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
            previewDate,
            userSegments
    );
  }
}
