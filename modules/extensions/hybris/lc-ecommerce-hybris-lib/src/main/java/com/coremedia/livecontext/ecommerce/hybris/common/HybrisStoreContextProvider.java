package com.coremedia.livecontext.ecommerce.hybris.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider;
import com.coremedia.blueprint.base.util.StructUtil;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.StoreConfigResource;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class HybrisStoreContextProvider extends AbstractStoreContextProvider {

  private StoreConfigResource storeConfigResource;

  private String defaultStoreId;
  private String defaultStoreLocale;

  private String previewDefaultCatalogVersion;
  private String liveDefaultCatalogVersion;

  @NonNull
  @Override
  protected Optional<StoreContext> internalCreateContext(@NonNull Site site) {
    // Only create store context if settings are found for current site.
    Struct repositoryStoreConfig = getSettingsService()
            .getSetting(CONFIG_KEY_STORE_CONFIG, Struct.class, site.getSiteRootDocument())
            .orElse(null);
    if (repositoryStoreConfig == null) {
      return Optional.empty();
    }

    StoreContextValuesHolder valuesHolder = new StoreContextValuesHolder();
    Map<String, Object> targetConfig = new HashMap<>();

    try {
      StructUtil.findString(repositoryStoreConfig, CONFIG_KEY_CONFIG_ID)
              .ifPresent(configId -> readStoreConfigFromSpring(configId, targetConfig));

      updateStoreConfigFromRepository(repositoryStoreConfig, targetConfig, site);

      valuesHolder.siteId = site.getId();
      valuesHolder.storeId = (String) targetConfig.get(CONFIG_KEY_STORE_ID);
      valuesHolder.storeName = (String) targetConfig.get(CONFIG_KEY_STORE_NAME);
      String catalogIdStr = (String) targetConfig.get(CONFIG_KEY_CATALOG_ID);
      valuesHolder.catalogId = catalogIdStr != null ? CatalogId.of(catalogIdStr) : null;
      valuesHolder.catalogVersion = getCatalogVersion(site);
      valuesHolder.locale = site.getLocale();
      String currencyStr = (String) targetConfig.get(CONFIG_KEY_CURRENCY);
      valuesHolder.currency = currencyStr != null ? parseCurrency(currencyStr) : null;

      StoreContext storeContext = createStoreContext(valuesHolder);
      return Optional.of(storeContext);
    } catch (NoSuchPropertyDescriptorException e) {
      throw new InvalidContextException("Missing properties in store configuration. ", e);
    }
  }

  private String getCatalogVersion(@NonNull Site site) {
    return isPreviewContentRepository(site) ? previewDefaultCatalogVersion : liveDefaultCatalogVersion;
  }

  private static boolean isPreviewContentRepository(@NonNull Site site) {
    return site.getSiteRootDocument().getRepository().isContentManagementServer();
  }

  /**
   * Adds the given values to a store context.
   * <p>
   * All values are optional. You can use a "null" value to omit single values.
   *
   * @return the new built store context
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if locale or currency has wrong format
   */
  @NonNull
  private static StoreContext createStoreContext(@NonNull StoreContextValuesHolder valuesHolder) {
    String siteId = valuesHolder.siteId;
    String storeId = valuesHolder.storeId;
    String storeName = valuesHolder.storeName;
    CatalogId catalogId = valuesHolder.catalogId;
    String catalogVersion = valuesHolder.catalogVersion;
    Locale locale = valuesHolder.locale;
    Currency currency = valuesHolder.currency;

    HybrisStoreContextBuilder builder = HybrisStoreContextBuilder.from(siteId);

    if (storeId != null) {
      if (StringUtils.isBlank(storeId)) {
        throw new InvalidContextException("storeId has wrong format: \"" + storeId + "\"");
      }

      builder.withStoreId(storeId);
    }

    if (storeName != null) {
      if (StringUtils.isBlank(storeName)) {
        throw new InvalidContextException("storeName has wrong format: \"" + storeId + "\"");
      }

      builder.withStoreName(storeName);
    }

    if (catalogId != null) {
      builder.withCatalogId(catalogId);
    }

    if (catalogVersion != null) {
      if (StringUtils.isBlank(catalogVersion)) {
        throw new InvalidContextException("catalogVersion has wrong format: \"" + catalogVersion + "\"");
      }

      builder.withCatalogVersion(catalogVersion);
    }

    if (currency != null) {
      builder.withCurrency(currency);
    }

    if (locale != null) {
      builder.withLocale(locale);
    }

    return builder.build();
  }

  @NonNull
  private static Currency parseCurrency(@NonNull String currencyCode) {
    try {
      return Currency.getInstance(currencyCode);
    } catch (IllegalArgumentException e) {
      throw new InvalidContextException(e);
    }
  }

  private static class StoreContextValuesHolder {

    private String siteId;
    private String storeId;
    private String storeName;
    private CatalogId catalogId;
    private String catalogVersion;
    private Locale locale;
    private Currency currency;
  }

  @NonNull
  @Override
  public StoreContextBuilder buildContext(@NonNull StoreContext source) {
    return HybrisStoreContextBuilder.from(source);
  }

  public StoreConfigResource getStoreConfigResource() {
    return storeConfigResource;
  }

  @Required
  public void setStoreConfigResource(StoreConfigResource storeConfigResource) {
    this.storeConfigResource = storeConfigResource;
  }

  public String getDefaultStoreId() {
    return defaultStoreId;
  }

  @Value("${livecontext.hybris.default.storeId:default}")
  public void setDefaultStoreId(String defaultStoreId) {
    this.defaultStoreId = defaultStoreId;
  }

  public String getDefaultStoreLocale() {
    return defaultStoreLocale;
  }

  @Value("${livecontext.hybris.default.locale:en_US}")
  public void setDefaultStoreLocale(String defaultStoreLocale) {
    this.defaultStoreLocale = defaultStoreLocale;
  }

  @Value("${livecontext.hybris.rest.defaultCatalagVersion.preview:Staged}")
  public void setPreviewDefaultCatalogVersion(String previewDefaultCatalogVersion) {
    this.previewDefaultCatalogVersion = previewDefaultCatalogVersion;
  }

  @Value("${livecontext.hybris.rest.defaultCatalagVersion.live:Online}")
  public void setLiveDefaultCatalogVersion(String liveDefaultCatalogVersion) {
    this.liveDefaultCatalogVersion = liveDefaultCatalogVersion;
  }
}

