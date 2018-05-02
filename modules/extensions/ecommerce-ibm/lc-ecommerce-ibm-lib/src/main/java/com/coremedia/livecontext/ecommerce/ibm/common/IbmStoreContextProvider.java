package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.SiteToStoreContextCacheKeyWithTimeout;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.base.util.StructUtil;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class IbmStoreContextProvider extends AbstractStoreContextProvider {

  private static final Logger LOG = LoggerFactory.getLogger(IbmStoreContextProvider.class);

  private StoreInfoService storeInfoService;

  @Nonnull
  @Override
  protected Optional<StoreContext> getStoreContextFromCache(@Nonnull Site site) {
    return findInCache(new SiteToStoreContextCacheKeyWithTimeout(site, this, 30));
  }

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
      StoreContextValuesHolder valuesHolder = new StoreContextValuesHolder();
      Map<String, Object> targetConfig = new HashMap<>();

      String configId = StructUtil.getString(repositoryStoreConfig, CONFIG_KEY_CONFIG_ID);

      readStoreConfigFromSpring(configId, targetConfig);
      updateStoreConfigFromRepository(repositoryStoreConfig, targetConfig, site);

      valuesHolder.storeId = (String) targetConfig.get(CONFIG_KEY_STORE_ID);
      valuesHolder.storeName = (String) targetConfig.get(CONFIG_KEY_STORE_NAME);
      valuesHolder.catalogId = (String) targetConfig.get(CONFIG_KEY_CATALOG_ID);
      valuesHolder.catalogName = (String) targetConfig.get(CONFIG_KEY_CATALOG_NAME);
      valuesHolder.currency = (String) targetConfig.get(CONFIG_KEY_CURRENCY);
      valuesHolder.workspaceId = (String) targetConfig.get(CONFIG_KEY_WORKSPACE_ID);
      valuesHolder.wcsVersion = (String) targetConfig.get(CONFIG_KEY_WCS_VERSION);
      valuesHolder.replacements = (Map<String, String>) targetConfig.get(CONFIG_KEY_REPLACEMENTS);

      updateStoreConfigFromDynamicStoreInfo(site.getName(), valuesHolder);

      return createStoreContext(valuesHolder, site);
    } catch (NoSuchPropertyDescriptorException e) {
      throw new InvalidContextException("Missing properties in store configuration. ", e);
    }
  }

  protected void updateStoreConfigFromDynamicStoreInfo(@Nonnull String siteName,
                                                       @Nonnull StoreContextValuesHolder valuesHolder) {
    if (storeInfoService == null || !storeInfoService.isAvailable()) {
      return;
    }

    String storeName = getStoreName(valuesHolder, siteName);

    updateStoreId(valuesHolder, storeName, siteName);
    updateCatalogId(valuesHolder, storeName, siteName);
    updateTimeZoneId(valuesHolder);
    updateWcsVersion(valuesHolder);
  }

  @Nonnull
  private static String getStoreName(@Nonnull StoreContextValuesHolder valuesHolder, @Nonnull String siteName) {
    String storeName = valuesHolder.storeName;

    if (isBlank(storeName)) {
      throw new InvalidContextException("No store name found in config (site: " + siteName + ").");
    }

    return storeName;
  }

  @Nonnull
  private Optional<String> getStoreId(@Nonnull StoreContextValuesHolder valuesHolder, @Nonnull String storeName,
                                      @Nonnull String siteName) {
    String storeIdFromConfig = valuesHolder.storeId;
    if (storeIdFromConfig != null) {
      return Optional.empty();
    }

    String storeId = storeInfoService.getStoreId(storeName);

    if (isBlank(storeId)) {
      throw new InvalidContextException(
              "No store id found for store '" + storeName + "' in WCS (site: " + siteName + ").");
    }

    return Optional.of(storeId);
  }

  private void updateStoreId(@Nonnull StoreContextValuesHolder valuesHolder, @Nonnull String storeName,
                             @Nonnull String siteName) {
    getStoreId(valuesHolder, storeName, siteName)
            .ifPresent(storeId -> valuesHolder.storeId = storeId);
  }

  @Nonnull
  private Optional<String> getCatalogId(@Nonnull StoreContextValuesHolder valuesHolder, @Nonnull String storeName,
                                        @Nonnull String siteName) {
    String catalogIdFromConfig = valuesHolder.catalogId;
    if (catalogIdFromConfig != null) {
      return Optional.empty();
    }

    String catalogId;

    String catalogName = valuesHolder.catalogName;
    if (catalogName != null) {
      catalogId = storeInfoService.getCatalogId(storeName, catalogName);

      if (isBlank(catalogId)) {
        throw new InvalidContextException("No catalog '" + catalogName + "' found in WCS (site: " + siteName + ").");
      }
    } else {
      catalogId = storeInfoService.getDefaultCatalogId(storeName);

      if (isBlank(catalogId)) {
        throw new InvalidContextException(
                "No default catalog id found for store '" + storeName + "' in WCS (site: " + siteName + ").");
      }
    }

    return Optional.of(catalogId);
  }

  private void updateCatalogId(@Nonnull StoreContextValuesHolder valuesHolder, @Nonnull String storeName,
                               @Nonnull String siteName) {
    getCatalogId(valuesHolder, storeName, siteName)
            .ifPresent(catalogId -> valuesHolder.catalogId = catalogId);
  }

  @Nonnull
  private ZoneId getTimeZoneId() {
    TimeZone timeZone = storeInfoService.getTimeZone();
    return timeZone.toZoneId();
  }

  private void updateTimeZoneId(@Nonnull StoreContextValuesHolder valuesHolder) {
    valuesHolder.timeZoneId = getTimeZoneId();
  }

  @Nonnull
  private Optional<String> getWcsVersion(@Nonnull StoreContextValuesHolder valuesHolder) {
    String storeInfoWcsVersion = storeInfoService.getWcsVersion();

    if (isBlank(storeInfoWcsVersion)) {
      LOG.info("No dynamic WCS version. Please update the StoreInfoHandler.");
      return Optional.empty();
    }

    String configWcsVersion = valuesHolder.wcsVersion;

    if (isNotBlank(configWcsVersion) && !storeInfoWcsVersion.equals(configWcsVersion)) {
      LOG.info("The configured WCS version '{}' is different from the version reading from the WCS system '{}'. " +
              "Delete the configuration for \"livecontext.ibm.wcs.version\" to avoid this message. " +
              "In the following we go with the version coming from the WCS system.",
              configWcsVersion, storeInfoWcsVersion);
    }

    return Optional.of(storeInfoWcsVersion);
  }

  private void updateWcsVersion(@Nonnull StoreContextValuesHolder valuesHolder) {
    getWcsVersion(valuesHolder)
            .ifPresent(wcsVersion -> valuesHolder.wcsVersion = wcsVersion);
  }

  @Nonnull
  private static StoreContext createStoreContext(@Nonnull StoreContextValuesHolder valuesHolder, @Nonnull Site site) {
    StoreContext context = StoreContextHelper.createContext(
            valuesHolder.storeId,
            valuesHolder.storeName,
            valuesHolder.catalogId,
            site.getLocale().toString(),
            valuesHolder.currency
    );

    StoreContextHelper.setSiteId(context, site.getId());
    ((StoreContextImpl) context).setTimeZoneId(valuesHolder.timeZoneId);
    StoreContextHelper.setWorkspaceId(context, valuesHolder.workspaceId);
    StoreContextHelper.setWcsVersion(context, valuesHolder.wcsVersion);
    StoreContextHelper.setReplacements(context, valuesHolder.replacements);

    return context;
  }

  private static class StoreContextValuesHolder {

    private String storeId;
    private String storeName;
    private String catalogId;
    private String catalogName;
    private String currency;
    private ZoneId timeZoneId;
    private String workspaceId;
    private String wcsVersion;
    private Map<String, String> replacements;
  }

  public void setStoreInfoService(StoreInfoService storeInfoService) {
    this.storeInfoService = storeInfoService;
  }
}
