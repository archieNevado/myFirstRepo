package com.coremedia.livecontext.ecommerce.ibm.storeinfo;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.catalog.CatalogName;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import edu.umd.cs.findbugs.annotations.NonNull;
import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

public class StoreInfoService {

  private WcStoreInfoWrapperService wrapperService;
  private CommerceCache commerceCache;
  private int delayOnError;
  private StoreInfoCacheKey storeInfoCacheKey;

  @NonNull
  public Optional<String> getStoreId(String storeName) {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    return DataMapHelper.findStringValue(storeInfos, "stores." + storeName + ".storeId");
  }

  @NonNull
  public Optional<CatalogId> getDefaultCatalogId(String storeName) {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    return DataMapHelper.findStringValue(storeInfos, "stores." + storeName + ".defaultCatalogId")
            .map(CatalogId::of);
  }

  @NonNull
  public Optional<CatalogName> getDefaultCatalogName(String storeName) {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    return DataMapHelper.findStringValue(storeInfos, "stores." + storeName + ".defaultCatalog")
            .map(CatalogName::of);
  }

  @NonNull
  public Optional<CatalogId> getCatalogId(String storeName, String catalogName) {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    return DataMapHelper.findStringValue(storeInfos, "stores." + storeName + ".catalogs." + catalogName)
            .map(CatalogId::of);
  }

  public Map<String, Object> getStoreInfos() {
    return commerceCache.get(storeInfoCacheKey);
  }

  @NonNull
  public TimeZone getTimeZone() {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    String sTimeZoneId = DataMapHelper.findStringValue(storeInfos, "serverTimezoneId").orElse(null);
    return TimeZone.getTimeZone(sTimeZoneId);
  }

  @NonNull
  public Optional<String> getWcsVersion() {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    return DataMapHelper.findStringValue(storeInfos, "wcsVersion");
  }

  public boolean isAvailable() {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    return !storeInfos.isEmpty();
  }

  public WcStoreInfoWrapperService getWrapperService() {
    return wrapperService;
  }

  @Autowired
  public void setWrapperService(WcStoreInfoWrapperService wrapperService) {
    this.wrapperService = wrapperService;
  }

  public CommerceCache getCommerceCache() {
    return commerceCache;
  }

  @Autowired
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  @Value("${livecontext.ibm.storeInfo.delayOnError:60}")
  public void setDelayOnError(int delayOnError) {
    this.delayOnError = delayOnError;
  }

  @PostConstruct
  void initialize() {
    storeInfoCacheKey = new StoreInfoCacheKey(AbstractCommerceCacheKey.CONFIG_KEY_STORE_INFO, wrapperService,
            commerceCache, delayOnError);
  }
}
