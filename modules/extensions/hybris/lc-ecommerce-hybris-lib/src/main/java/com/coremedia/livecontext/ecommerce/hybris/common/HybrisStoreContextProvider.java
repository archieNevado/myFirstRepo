package com.coremedia.livecontext.ecommerce.hybris.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.CommerceConfigKeys;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.hybris.rest.resources.StoreConfigResource;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@DefaultAnnotation(NonNull.class)
public class HybrisStoreContextProvider extends AbstractStoreContextProvider {

  @Nullable
  private StoreConfigResource storeConfigResource;

  @Nullable
  private String defaultStoreId;
  @Nullable
  private String defaultStoreLocale;

  @Nullable
  private String previewDefaultCatalogVersion;
  @Nullable
  private String liveDefaultCatalogVersion;

  @Override
  protected Optional<StoreContext> internalCreateContext(Site site) {
    // Only create store context if settings are found for current site.
    return Optional.of(findRepositoryStoreConfig(site))
            .filter(config -> !config.isEmpty())
            .map(config -> buildContextFromRepositoryStoreConfig(site, config));
  }

  private StoreContext buildContextFromRepositoryStoreConfig(Site site, Map<String, Object> repositoryStoreConfig) {
    Map<String, Object> targetConfig = new HashMap<>();

    findConfigId(repositoryStoreConfig)
            .map(this::readStoreConfigFromSpring)
            .ifPresent(targetConfig::putAll);

    updateStoreConfigFromRepository(repositoryStoreConfig, targetConfig);

    StoreContextValuesHolder valuesHolder = populateValuesHolder(targetConfig, site);

    return createStoreContext(valuesHolder);
  }

  private StoreContextValuesHolder populateValuesHolder(Map<String, Object> config, Site site) {
    StoreContextValuesHolder valuesHolder = new StoreContextValuesHolder();

    valuesHolder.siteId = site.getId();
    valuesHolder.storeId = (String) config.get(CommerceConfigKeys.STORE_ID);
    valuesHolder.storeName = (String) config.get(CommerceConfigKeys.STORE_NAME);
    String catalogIdStr = (String) config.get(CommerceConfigKeys.CATALOG_ID);
    valuesHolder.catalogId = catalogIdStr != null ? CatalogId.of(catalogIdStr) : null;
    valuesHolder.catalogVersion = getCatalogVersion(site);
    String currencyStr = (String) config.get(CommerceConfigKeys.CURRENCY);
    valuesHolder.currency = currencyStr != null ? parseCurrency(currencyStr) : null;
    valuesHolder.locale = site.getLocale();

    return valuesHolder;
  }

  private String getCatalogVersion(Site site) {
    return isPreviewContentRepository(site) ? previewDefaultCatalogVersion : liveDefaultCatalogVersion;
  }

  private static boolean isPreviewContentRepository(Site site) {
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
  private static StoreContext createStoreContext(StoreContextValuesHolder valuesHolder) {
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

  private static Currency parseCurrency(String currencyCode) {
    try {
      return Currency.getInstance(currencyCode);
    } catch (IllegalArgumentException e) {
      throw new InvalidContextException(e);
    }
  }

  private static class StoreContextValuesHolder {

    @Nullable
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
  }

  @Override
  public StoreContextBuilder buildContext(StoreContext source) {
    if (!(source instanceof StoreContextImpl)) {
      throw new IllegalArgumentException(
              String.format("Store context must be an instance of `%s`.", StoreContextImpl.class.getSimpleName()));
    }

    return HybrisStoreContextBuilder.from((StoreContextImpl) source);
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

