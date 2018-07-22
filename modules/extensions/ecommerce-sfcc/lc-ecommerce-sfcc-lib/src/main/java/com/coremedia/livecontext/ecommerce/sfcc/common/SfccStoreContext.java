package com.coremedia.livecontext.ecommerce.sfcc.common;

import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import com.google.common.collect.ImmutableMap;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * An SFCC-specific store context.
 */
public class SfccStoreContext implements StoreContext {

  private final ImmutableMap<String, String> replacements;
  private final String siteId;
  private final String storeId;
  private final String storeName;
  private final CatalogId catalogId;
  private final CatalogAlias catalogAlias;
  private final Currency currency;
  private final Locale locale;
  private ZonedDateTime previewDate;
  private String userSegments;

  SfccStoreContext(
          @NonNull Map<String, String> replacements,
          @NonNull String siteId,
          @NonNull String storeId,
          @NonNull String storeName,
          @NonNull CatalogId catalogId,
          @NonNull CatalogAlias catalogAlias,
          @NonNull Currency currency,
          @NonNull Locale locale,
          @Nullable ZonedDateTime previewDate,
          @Nullable String userSegments) {
    this.replacements = ImmutableMap.copyOf(replacements);
    this.siteId = siteId;
    this.storeId = storeId;
    this.storeName = storeName;
    this.catalogId = catalogId;
    this.catalogAlias = catalogAlias;
    this.currency = currency;
    this.locale = locale;
    this.previewDate = previewDate;
    this.userSegments = userSegments;
  }

  @Override
  public Object get(@NonNull String name) {
    // Always return `null` for now. Only implement if unavoidable, e.g. using reflection.
    return null;
  }

  @Override
  public void put(@NonNull String name, Object value) {
    // Nothing to do, instance is immutable.
  }

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  public Map<String, String> getReplacements() {
    return replacements;
  }

  @Override
  public String getSiteId() {
    return siteId;
  }

  @Override
  public void setSiteId(String siteId) {
    // Nothing to do, instance is immutable.
  }

  @Override
  public String getStoreId() {
    return storeId;
  }

  @Override
  public String getStoreName() {
    return storeName;
  }

  @Override
  public String getCatalogId() {
    return catalogId.value();
  }

  @Override
  public CatalogAlias getCatalogAlias() {
    return catalogAlias;
  }

  @Override
  public void setCatalog(@Nullable CatalogAlias catalogAlias, @Nullable CatalogId catalogId) {
    // Nothing to do, instance is immutable.
  }

  @Override
  public String getCatalogVersion() {
    return null;
  }

  @Override
  public Currency getCurrency() {
    return currency;
  }

  @Override
  public Locale getLocale() {
    return locale;
  }

  @NonNull
  @Override
  public Optional<ZoneId> getTimeZoneId() {
    return Optional.empty();
  }

  @Override
  public boolean hasPreviewContext() {
    return false;
  }

  @NonNull
  @Override
  public Optional<WorkspaceId> getWorkspaceId() {
    return Optional.empty();
  }

  @Override
  public void setWorkspaceId(@Nullable WorkspaceId workspaceId) {
    // Nothing to do, instance is immutable.
  }

  @NonNull
  @Override
  public Optional<ZonedDateTime> getPreviewDate() {
    return Optional.empty();
  }

  @Override
  public void setPreviewDate(@Nullable ZonedDateTime previewDate) {
    // For now, accept modification of the context for legacy reasons. Shall be changed soon.
    this.previewDate = previewDate;
  }

  @Override
  public String getUserSegments() {
    return userSegments;
  }

  @Override
  public void setUserSegments(String userSegments) {
    // Nothing to do, instance is immutable.
  }

  @Override
  public String getConnectionId() {
    return null;
  }

  @NonNull
  @Override
  public List<String> getContractIds() {
    return emptyList();
  }

  @Override
  public void setContractIds(@NonNull List<String> contractIds) {
    // Nothing to do, instance is immutable.
  }

  @NonNull
  @Override
  public List<String> getContractIdsForPreview() {
    return emptyList();
  }

  @Override
  public void setContractIdsForPreview(@NonNull List<String> contractIds) {
    // Nothing to do, instance is immutable.
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SfccStoreContext that = (SfccStoreContext) o;
    return Objects.equals(replacements, that.replacements) &&
            Objects.equals(siteId, that.siteId) &&
            Objects.equals(storeId, that.storeId) &&
            Objects.equals(storeName, that.storeName) &&
            Objects.equals(catalogId, that.catalogId) &&
            Objects.equals(catalogAlias, that.catalogAlias) &&
            Objects.equals(currency, that.currency) &&
            Objects.equals(locale, that.locale) &&
            Objects.equals(previewDate, that.previewDate) &&
            Objects.equals(userSegments, that.userSegments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(replacements, siteId, storeId, storeName, catalogId, catalogAlias, currency, locale,
            previewDate, userSegments);
  }
}
