package com.coremedia.livecontext.ecommerce.sfcc.common;

import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * An SFCC-specific store context.
 */
public class SfccStoreContext implements StoreContext {

  private final ImmutableMap<String, String> replacements;
  private final String siteId;
  private final String configId;
  private final String storeId;
  private final String storeName;
  private final CatalogId catalogId;
  private final CatalogAlias catalogAlias;
  private final Currency currency;
  private final Locale locale;
  private String previewDate;

  SfccStoreContext(
          @Nonnull Map<String, String> replacements,
          @Nonnull String siteId, String configId,
          @Nonnull String storeId,
          @Nonnull String storeName,
          @Nonnull CatalogId catalogId,
          @Nonnull CatalogAlias catalogAlias,
          @Nonnull Currency currency,
          @Nonnull Locale locale,
          @Nullable String previewDate) {
    this.replacements = ImmutableMap.copyOf(replacements);
    this.siteId = siteId;
    this.configId = configId;
    this.storeId = storeId;
    this.storeName = storeName;
    this.catalogId = catalogId;
    this.catalogAlias = catalogAlias;
    this.currency = currency;
    this.locale = locale;
    this.previewDate = previewDate;
  }

  @Override
  public Object get(@Nonnull String name) {
    // Always return `null` for now. Only implement if unavoidable, e.g. using reflection.
    return null;
  }

  @Override
  public void put(@Nonnull String name, Object value) {
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
  public String getConfigId() {
    return configId;
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

  @Override
  public boolean hasPreviewContext() {
    return false;
  }

  @Override
  public String getWorkspaceId() {
    return null;
  }

  @Override
  public void setWorkspaceId(String workspaceId) {
    // Nothing to do, instance is immutable.
  }

  @Override
  public String getPreviewDate() {
    return previewDate;
  }

  @Override
  public void setPreviewDate(@Nullable String previewDate) {
    // For now, accept modification of the context for legacy reasons. Shall be changed soon.
    this.previewDate = previewDate;
  }

  @Override
  public String getUserSegments() {
    return null;
  }

  @Override
  public void setUserSegments(String userSegments) {
    // Nothing to do, instance is immutable.
  }

  @Override
  public String getConnectionId() {
    return null;
  }

  @Override
  public String[] getContractIds() {
    return new String[0];
  }

  @Override
  public void setContractIds(String[] contractIds) {
    // Nothing to do, instance is immutable.
  }

  @Override
  public String[] getContractIdsForPreview() {
    return new String[0];
  }

  @Override
  public void setContractIdsForPreview(String[] contractIds) {
    // Nothing to do, instance is immutable.
  }

  @Override
  public StoreContext getClone() {
    throw new UnsupportedOperationException();
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
            Objects.equals(configId, that.configId) &&
            Objects.equals(storeId, that.storeId) &&
            Objects.equals(storeName, that.storeName) &&
            Objects.equals(catalogId, that.catalogId) &&
            Objects.equals(catalogAlias, that.catalogAlias) &&
            Objects.equals(currency, that.currency) &&
            Objects.equals(locale, that.locale) &&
            Objects.equals(previewDate, that.previewDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(replacements, siteId, configId, storeId, storeName, catalogId, catalogAlias, currency, locale,
            previewDate);
  }
}
