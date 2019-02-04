package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.SiteToStoreContextCacheKeyWithTimeout;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.catalog.CatalogName;
import com.coremedia.livecontext.ecommerce.common.CommerceConfigKeys;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoService;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.WORKSPACE_ID_NONE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@DefaultAnnotation(NonNull.class)
public class IbmStoreContextProvider extends AbstractStoreContextProvider {

  private static final Logger LOG = LoggerFactory.getLogger(IbmStoreContextProvider.class);

  @Nullable
  private StoreInfoService storeInfoService;

  @Override
  protected Optional<StoreContext> getStoreContextFromCache(Site site) {
    return findInCache(new SiteToStoreContextCacheKeyWithTimeout(site, this, 30));
  }

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

    StoreContextValuesHolder valuesHolder = populateValuesHolder(targetConfig);

    updateStoreConfigFromDynamicStoreInfo(site.getName(), valuesHolder);

    return createStoreContext(valuesHolder, site);
  }

  private static StoreContextValuesHolder populateValuesHolder(Map<String, Object> config) {
    StoreContextValuesHolder valuesHolder = new StoreContextValuesHolder();

    valuesHolder.storeId = (String) config.get(CommerceConfigKeys.STORE_ID);
    valuesHolder.storeName = (String) config.get(CommerceConfigKeys.STORE_NAME);
    String catalogIdStr = (String) config.get(CommerceConfigKeys.CATALOG_ID);
    valuesHolder.catalogId = catalogIdStr != null ? CatalogId.of(catalogIdStr) : null;
    String catalogNameStr = (String) config.get(CommerceConfigKeys.CATALOG_NAME);
    valuesHolder.catalogName = catalogNameStr != null ? CatalogName.of(catalogNameStr) : null;
    String currencyStr = (String) config.get(CommerceConfigKeys.CURRENCY);
    valuesHolder.currency = currencyStr != null ? parseCurrency(currencyStr) : null;
    String workspaceIdStr = (String) config.get(CommerceConfigKeys.WORKSPACE_ID);
    valuesHolder.workspaceId = workspaceIdStr != null ? WorkspaceId.of(workspaceIdStr) : WORKSPACE_ID_NONE;
    valuesHolder.wcsVersion = (String) config.get(CONFIG_KEY_WCS_VERSION);
    valuesHolder.replacements = (Map<String, String>) config.get(CommerceConfigKeys.REPLACEMENTS);

    return valuesHolder;
  }

  protected void updateStoreConfigFromDynamicStoreInfo(String siteName, StoreContextValuesHolder valuesHolder) {
    if (storeInfoService == null || !storeInfoService.isAvailable()) {
      return;
    }

    String storeName = getStoreName(valuesHolder, siteName);

    getStoreId(valuesHolder, storeName, siteName).ifPresent(storeId -> valuesHolder.storeId = storeId);
    getCatalogId(valuesHolder, storeName, siteName).ifPresent(catalogId -> valuesHolder.catalogId = catalogId);
    valuesHolder.timeZoneId = storeInfoService.getTimeZone().toZoneId();
    getWcsVersion(valuesHolder).ifPresent(wcsVersion -> valuesHolder.wcsVersion = wcsVersion);
  }

  private static String getStoreName(StoreContextValuesHolder valuesHolder, String siteName) {
    String storeName = valuesHolder.storeName;

    if (isBlank(storeName)) {
      throw new InvalidContextException("No store name found in config (site: " + siteName + ").");
    }

    return storeName;
  }

  private Optional<String> getStoreId(StoreContextValuesHolder valuesHolder, String storeName, String siteName) {
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

  private Optional<CatalogId> getCatalogId(StoreContextValuesHolder valuesHolder, String storeName, String siteName) {
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

  private static Currency parseCurrency(String currencyCode) {
    try {
      return Currency.getInstance(currencyCode);
    } catch (IllegalArgumentException e) {
      throw new InvalidContextException(e);
    }
  }

  private Optional<String> getWcsVersion(StoreContextValuesHolder valuesHolder) {
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

  private static StoreContext createStoreContext(StoreContextValuesHolder valuesHolder, Site site) {
    IbmStoreContextBuilder builder = StoreContextHelper
            .buildContext(
                    site.getId(),
                    valuesHolder.storeId,
                    valuesHolder.storeName,
                    valuesHolder.catalogId,
                    site.getLocale(),
                    valuesHolder.currency
            )
            .withTimeZoneId(valuesHolder.timeZoneId)
            .withWorkspaceId(valuesHolder.workspaceId)
            .withReplacements(valuesHolder.replacements);

    if (valuesHolder.wcsVersion != null) {
      WcsVersion.fromVersionString(valuesHolder.wcsVersion)
              .ifPresent(builder::withWcsVersion);
    }

    return builder.build();
  }

  private static class StoreContextValuesHolder {

    @Nullable
    private String storeId;
    @Nullable
    private String storeName;
    @Nullable
    private CatalogId catalogId;
    @Nullable
    private CatalogName catalogName;
    @Nullable
    private Currency currency;
    @Nullable
    private ZoneId timeZoneId;
    @Nullable
    private WorkspaceId workspaceId;
    @Nullable
    private String wcsVersion;
    @Nullable
    private Map<String, String> replacements;
  }

  public void setStoreInfoService(StoreInfoService storeInfoService) {
    this.storeInfoService = storeInfoService;
  }
}
