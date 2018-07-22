package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.SiteToStoreContextCacheKeyWithTimeout;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.base.util.StructUtil;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.catalog.CatalogName;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoService;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.time.ZoneId;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.WORKSPACE_ID_NONE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class IbmStoreContextProvider extends AbstractStoreContextProvider {

  private static final Logger LOG = LoggerFactory.getLogger(IbmStoreContextProvider.class);

  private StoreInfoService storeInfoService;

  @NonNull
  @Override
  protected Optional<StoreContext> getStoreContextFromCache(@NonNull Site site) {
    return findInCache(new SiteToStoreContextCacheKeyWithTimeout(site, this, 30));
  }

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

    try {
      StoreContextValuesHolder valuesHolder = new StoreContextValuesHolder();
      Map<String, Object> targetConfig = new HashMap<>();

      StructUtil.findString(repositoryStoreConfig, CONFIG_KEY_CONFIG_ID)
              .ifPresent(configId -> readStoreConfigFromSpring(configId, targetConfig));

      updateStoreConfigFromRepository(repositoryStoreConfig, targetConfig, site);

      valuesHolder.storeId = (String) targetConfig.get(CONFIG_KEY_STORE_ID);
      valuesHolder.storeName = (String) targetConfig.get(CONFIG_KEY_STORE_NAME);
      String catalogIdStr = (String) targetConfig.get(CONFIG_KEY_CATALOG_ID);
      valuesHolder.catalogId = catalogIdStr != null ? CatalogId.of(catalogIdStr) : null;
      String catalogNameStr = (String) targetConfig.get(CONFIG_KEY_CATALOG_NAME);
      valuesHolder.catalogName = catalogNameStr != null ? CatalogName.of(catalogNameStr) : null;
      String currencyStr = (String) targetConfig.get(CONFIG_KEY_CURRENCY);
      valuesHolder.currency = currencyStr != null ? parseCurrency(currencyStr) : null;
      String workspaceIdStr = (String) targetConfig.get(CONFIG_KEY_WORKSPACE_ID);
      valuesHolder.workspaceId = workspaceIdStr != null ? WorkspaceId.of(workspaceIdStr) : WORKSPACE_ID_NONE;
      valuesHolder.wcsVersion = (String) targetConfig.get(CONFIG_KEY_WCS_VERSION);
      valuesHolder.replacements = (Map<String, String>) targetConfig.get(CONFIG_KEY_REPLACEMENTS);

      updateStoreConfigFromDynamicStoreInfo(site.getName(), valuesHolder);

      StoreContext storeContext = createStoreContext(valuesHolder, site);
      return Optional.of(storeContext);
    } catch (NoSuchPropertyDescriptorException e) {
      throw new InvalidContextException("Missing properties in store configuration. ", e);
    }
  }

  protected void updateStoreConfigFromDynamicStoreInfo(@NonNull String siteName,
                                                       @NonNull StoreContextValuesHolder valuesHolder) {
    if (storeInfoService == null || !storeInfoService.isAvailable()) {
      return;
    }

    String storeName = getStoreName(valuesHolder, siteName);

    updateStoreId(valuesHolder, storeName, siteName);
    updateCatalogId(valuesHolder, storeName, siteName);
    updateTimeZoneId(valuesHolder);
    updateWcsVersion(valuesHolder);
  }

  @NonNull
  private static String getStoreName(@NonNull StoreContextValuesHolder valuesHolder, @NonNull String siteName) {
    String storeName = valuesHolder.storeName;

    if (isBlank(storeName)) {
      throw new InvalidContextException("No store name found in config (site: " + siteName + ").");
    }

    return storeName;
  }

  @NonNull
  private Optional<String> getStoreId(@NonNull StoreContextValuesHolder valuesHolder, @NonNull String storeName,
                                      @NonNull String siteName) {
    String storeIdFromConfig = valuesHolder.storeId;
    if (storeIdFromConfig != null) {
      return Optional.empty();
    }

    Optional<String> storeId = storeInfoService.getStoreId(storeName);

    if (!storeId.isPresent() || isBlank(storeId.get())) {
      throw new InvalidContextException(
              "No store id found for store '" + storeName + "' in WCS (site: " + siteName + ").");
    }

    return storeId;
  }

  private void updateStoreId(@NonNull StoreContextValuesHolder valuesHolder, @NonNull String storeName,
                             @NonNull String siteName) {
    getStoreId(valuesHolder, storeName, siteName)
            .ifPresent(storeId -> valuesHolder.storeId = storeId);
  }

  @NonNull
  private Optional<CatalogId> getCatalogId(@NonNull StoreContextValuesHolder valuesHolder, @NonNull String storeName,
                                           @NonNull String siteName) {
    CatalogId catalogIdFromConfig = valuesHolder.catalogId;
    if (catalogIdFromConfig != null) {
      return Optional.empty();
    }

    Optional<CatalogId> catalogId;

    CatalogName catalogName = valuesHolder.catalogName;
    if (catalogName != null) {
      catalogId = storeInfoService.getCatalogId(storeName, catalogName.value());

      if (!catalogId.isPresent() || isBlank(catalogId.get().value())) {
        throw new InvalidContextException(
                "No catalog '" + catalogName.value() + "' found in WCS (site: " + siteName + ").");
      }
    } else {
      catalogId = storeInfoService.getDefaultCatalogId(storeName);

      if (!catalogId.isPresent() || isBlank(catalogId.get().value())) {
        throw new InvalidContextException(
                "No default catalog id found for store '" + storeName + "' in WCS (site: " + siteName + ").");
      }
    }

    return catalogId;
  }

  private void updateCatalogId(@NonNull StoreContextValuesHolder valuesHolder, @NonNull String storeName,
                               @NonNull String siteName) {
    getCatalogId(valuesHolder, storeName, siteName)
            .ifPresent(catalogId -> valuesHolder.catalogId = catalogId);
  }

  @NonNull
  private ZoneId getTimeZoneId() {
    TimeZone timeZone = storeInfoService.getTimeZone();
    return timeZone.toZoneId();
  }

  private void updateTimeZoneId(@NonNull StoreContextValuesHolder valuesHolder) {
    valuesHolder.timeZoneId = getTimeZoneId();
  }

  @NonNull
  private static Currency parseCurrency(@NonNull String currencyCode) {
    try {
      return Currency.getInstance(currencyCode);
    } catch (IllegalArgumentException e) {
      throw new InvalidContextException(e);
    }
  }

  @NonNull
  private Optional<String> getWcsVersion(@NonNull StoreContextValuesHolder valuesHolder) {
    Optional<String> storeInfoWcsVersion = storeInfoService.getWcsVersion();

    if (!storeInfoWcsVersion.isPresent() || isBlank(storeInfoWcsVersion.get())) {
      LOG.info("No dynamic WCS version. Please update the StoreInfoHandler.");
      return Optional.empty();
    }

    String configWcsVersion = valuesHolder.wcsVersion;

    if (isNotBlank(configWcsVersion) && !storeInfoWcsVersion.get().equals(configWcsVersion)) {
      LOG.info("The configured WCS version '{}' is different from the version reading from the WCS system '{}'. " +
              "Delete the configuration for \"livecontext.ibm.wcs.version\" to avoid this message. " +
              "In the following we go with the version coming from the WCS system.",
              configWcsVersion, storeInfoWcsVersion);
    }

    return storeInfoWcsVersion;
  }

  private void updateWcsVersion(@NonNull StoreContextValuesHolder valuesHolder) {
    getWcsVersion(valuesHolder)
            .ifPresent(wcsVersion -> valuesHolder.wcsVersion = wcsVersion);
  }

  @NonNull
  private static StoreContext createStoreContext(@NonNull StoreContextValuesHolder valuesHolder, @NonNull Site site) {
    StoreContext context = StoreContextHelper.createContext(
            site.getId(),
            valuesHolder.storeId,
            valuesHolder.storeName,
            valuesHolder.catalogId,
            site.getLocale().toString(),
            valuesHolder.currency
    );

    ((StoreContextImpl) context).setTimeZoneId(valuesHolder.timeZoneId);
    context.setWorkspaceId(valuesHolder.workspaceId);
    if (valuesHolder.wcsVersion != null) {
      StoreContextHelper.setWcsVersion(context, valuesHolder.wcsVersion);
    }
    StoreContextHelper.setReplacements(context, valuesHolder.replacements);

    return context;
  }

  private static class StoreContextValuesHolder {

    private String storeId;
    private String storeName;
    private CatalogId catalogId;
    private CatalogName catalogName;
    private Currency currency;
    private ZoneId timeZoneId;
    private WorkspaceId workspaceId;
    private String wcsVersion;
    private Map<String, String> replacements;
  }

  public void setStoreInfoService(StoreInfoService storeInfoService) {
    this.storeInfoService = storeInfoService;
  }
}
