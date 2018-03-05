package com.coremedia.livecontext.ecommerce.sfcc.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Currency;

/**
 * Subclass this class to add your own store context configuration.
 * May be overwritten via Content LiveContext Settings Document.
 * @see com.coremedia.livecontext.ecommerce.sfcc.common.SfccStoreContextProvider
 */
@ConfigurationProperties(prefix = "livecontext.sfcc.context")
public abstract class SfccStoreContextProperties {

  private String storeName;
  private String storeId;
  private String catalogId;
  private String currency;

  /**
   * @return the store name (e.g. SiteGenesis Global)
   */
  public String getStoreName() {
    return storeName;
  }

  /**
   * @return the store id (e.g. SiteGenesisGlobal)
   */
  public String getStoreId() {
    return storeId;
  }

  /**
   * @return the catalog id (e.g. storefront-catalog-non-en)
   */
  public String getCatalogId() {
    return catalogId;
  }

  /**
   * @return the currency as string {@link Currency#getCurrencyCode()} (e.g. GBP)
   */
  public String getCurrency() {
    return currency;
  }

  public abstract String getConfigId();

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  public void setStoreId(String storeId) {
    this.storeId = storeId;
  }

  public void setCatalogId(String catalogId) {
    this.catalogId = catalogId;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }
}
