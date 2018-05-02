package com.coremedia.livecontext.ecommerce.sfcc.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider;
import com.coremedia.blueprint.base.util.StructUtil;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccStoreContextProperties;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CatalogsResource;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService.DEFAULT_CATALOG_ALIAS;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CATALOG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.CURRENCY;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.LOCALE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.REPLACEMENTS;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.SITE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.STORE_NAME;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class SfccStoreContextProvider extends AbstractStoreContextProvider {

  private static final Logger LOG = LoggerFactory.getLogger(SfccStoreContextProvider.class);

  private static final int SITE_TO_CATALOG_CACHE_TIME = 300;

  private CatalogsResource catalogsResource;

  private List<SfccStoreContextProperties> storeContextConfigurations;

  private Map<String, SfccStoreContextProperties> storeContextConfigurationsByName;

  @Nullable
  @Override
  protected StoreContext internalCreateContext(@Nonnull Site site) {
    // only create catalog context if settings were found for current site
    Struct repositoryStoreConfig = getSettingsService().setting(CONFIG_KEY_STORE_CONFIG, Struct.class,
            site.getSiteRootDocument());

    if (repositoryStoreConfig == null) {
      return null;
    }

    try {
      Map<String, Object> targetConfig = new HashMap<>();

      String configId = StructUtil.getString(repositoryStoreConfig, CONFIG_KEY_CONFIG_ID);

      // Read store context configuration from spring and property files
      readStoreConfigFromSpring(configId, targetConfig);

      // Update store context configuration from LiveContext settings
      updateStoreConfigFromRepository(repositoryStoreConfig, targetConfig, site);

      return createContext(site, targetConfig);
    } catch (NoSuchPropertyDescriptorException e) {
      throw new InvalidContextException("Missing properties in store configuration.", e);
    }
  }

  @Override
  protected void readStoreConfigFromSpring(@Nullable String configId, @Nonnull Map<String, Object> targetStoreConfig) {
    if (configId == null) {
      return;
    }

    SfccStoreContextProperties sfccStoreContextProperties = storeContextConfigurationsByName.get(configId);
    if (sfccStoreContextProperties != null) {
      updateTargetConfig(sfccStoreContextProperties, targetStoreConfig);
    }
  }

  private static void updateTargetConfig(@Nonnull SfccStoreContextProperties config,
                                         @Nonnull Map<String, Object> targetStoreConfig) {
    putValueIfNotNull(CONFIG_KEY_STORE_ID, config.getStoreId(), targetStoreConfig);
    putValueIfNotNull(CONFIG_KEY_STORE_NAME, config.getStoreName(), targetStoreConfig);
    putValueIfNotNull(CONFIG_KEY_CATALOG_ID, config.getCatalogId(), targetStoreConfig);
    putValueIfNotNull(CONFIG_KEY_CURRENCY, config.getCurrency(), targetStoreConfig);
  }

  @Nonnull
  private StoreContext createContext(@Nonnull Site site, @Nonnull Map<String, Object> storeConfig) {
    storeConfig.put(SITE, site.getId());
    storeConfig.put(LOCALE, site.getLocale());

    StoreContextValuesHolder valuesHolder = new StoreContextValuesHolder();
    Map<String, String> storeContextReplacements = new HashMap<>();

    // Store all provided values in the context.
    for (Map.Entry<String, Object> configEntry : storeConfig.entrySet()) {
      String configEntryKey = configEntry.getKey();
      Object configEntryValue = configEntry.getValue();

      if (configEntryValue == null) {
        LOG.warn("Skipping null value for store config with key '{}'", configEntryKey);
        continue;
      }

      if (configEntryValue instanceof String) {
        String configValueString = (String) configEntryValue;
        handleConfigStringValueEntry(configEntryKey, configValueString, valuesHolder, storeContextReplacements);
      } else {
        handleConfigNonStringValueEntry(configEntryKey, configEntryValue, valuesHolder, storeContextReplacements);
      }
    }

    // Obtain catalog if not set yet.
    if (valuesHolder.catalogId == null) {
      valuesHolder.catalogId = getCatalogIdForStoreId(valuesHolder.storeId);
      valuesHolder.catalogAlias = DEFAULT_CATALOG_ALIAS;
    }

    return createStoreContext(valuesHolder, storeContextReplacements);
  }

  private static void handleConfigStringValueEntry(@Nonnull String configEntryKey, @Nonnull String configValueString,
                                                   @Nonnull StoreContextValuesHolder valuesHolder,
                                                   @Nonnull Map<String, String> storeContextReplacements) {
    if (isBlank(configValueString)) {
      LOG.warn("Skipping invalid value for store config with key '{}'", configEntryKey);
      return;
    }

    try {
      switch (configEntryKey) {
        case LOCALE:
          valuesHolder.locale = LocaleUtils.toLocale(configValueString);
          storeContextReplacements.put(LOCALE, configValueString);
          return;

        case CURRENCY:
          valuesHolder.currency = Currency.getInstance(configValueString);
          storeContextReplacements.put(CURRENCY, configValueString);
          return;

        case CONFIG_KEY_STORE_ID:
          valuesHolder.storeId = configValueString;
          storeContextReplacements.put(STORE_ID, configValueString);
          return;

        case CONFIG_KEY_STORE_NAME:
          valuesHolder.storeName = configValueString;
          storeContextReplacements.put(STORE_NAME, configValueString);
          return;

        case CONFIG_KEY_CATALOG_ID:
          valuesHolder.catalogId = CatalogId.of(configValueString);
          valuesHolder.catalogAlias = DEFAULT_CATALOG_ALIAS;
          storeContextReplacements.put(CATALOG_ID, configValueString);
          return;

        case SITE:
          valuesHolder.siteId = configValueString;
          storeContextReplacements.put(SITE, configValueString);
          return;

        default:
          storeContextReplacements.put(configEntryKey, configValueString);
      }
    } catch (IllegalArgumentException e) {
      throw new InvalidContextException(configEntryKey + " has wrong format.", e);
    }
  }

  private static void handleConfigNonStringValueEntry(@Nonnull String configEntryKey, @Nonnull Object configEntryValue,
                                                      @Nonnull StoreContextValuesHolder valuesHolder,
                                                      @Nonnull Map<String, String> storeContextReplacements) {
    switch (configEntryKey) {
      case CURRENCY:
        if (configEntryValue instanceof Currency) {
          Currency currency = (Currency) configEntryValue;
          valuesHolder.currency = currency;
          storeContextReplacements.put(CURRENCY, currency.getCurrencyCode());
        }
        return;

      case LOCALE:
        if (configEntryValue instanceof Locale) {
          Locale locale = (Locale) configEntryValue;
          valuesHolder.locale = locale;
          storeContextReplacements.put(LOCALE, locale.toLanguageTag().replaceAll("-", "_"));
        }
        return;

      case REPLACEMENTS:
        if (configEntryValue instanceof Map) {
          storeContextReplacements.putAll((Map<? extends String, ? extends String>) configEntryValue);
        }
        return;

      default:
    }
  }

  @Nonnull
  private CatalogId getCatalogIdForStoreId(@Nullable String storeId) {
    SiteToCatalogCacheKey cacheKey = new SiteToCatalogCacheKey("SiteToCatalogCacheKey", catalogsResource,
            SITE_TO_CATALOG_CACHE_TIME);

    String catalogId = getCache().get(cacheKey).get(storeId);
    if (isBlank(catalogId)) {
      throw new IllegalStateException("Unable to fetch 'catalogId' for store " + storeId);
    }

    return CatalogId.of(catalogId);
  }

  @Nonnull
  private static StoreContext createStoreContext(@Nonnull StoreContextValuesHolder valuesHolder,
                                                 @Nonnull Map<String, String> replacements) {
    return SfccStoreContextBuilder
            .from(
                    replacements,
                    valuesHolder.siteId,
                    valuesHolder.storeId,
                    valuesHolder.storeName,
                    valuesHolder.catalogId,
                    valuesHolder.catalogAlias,
                    valuesHolder.currency,
                    valuesHolder.locale
            )
            .build();
  }

  private static class StoreContextValuesHolder {

    private String siteId;
    private String storeId;
    private CatalogId catalogId;
    private CatalogAlias catalogAlias;
    private String storeName;
    private Currency currency;
    private Locale locale;
  }

  @Nonnull
  @Override
  public StoreContextBuilder buildContext(@Nonnull StoreContext source) {
    return SfccStoreContextBuilder.from(source);
  }

  @Nonnull
  @Override
  public StoreContext cloneContext(@Nonnull StoreContext source) {
    return buildContext(source)
            .build();
  }

  @Autowired
  public void setCatalogsResource(CatalogsResource catalogsResource) {
    this.catalogsResource = catalogsResource;
  }

  @Autowired
  public void setStoreContextConfigurations(List<SfccStoreContextProperties> storeContextConfigurations) {
    this.storeContextConfigurations = storeContextConfigurations;
  }

  @PostConstruct
  void initialize() {
    storeContextConfigurationsByName = storeContextConfigurations.stream()
            .collect(Collectors.toMap(SfccStoreContextProperties::getConfigId, Function.identity()));
  }
}

