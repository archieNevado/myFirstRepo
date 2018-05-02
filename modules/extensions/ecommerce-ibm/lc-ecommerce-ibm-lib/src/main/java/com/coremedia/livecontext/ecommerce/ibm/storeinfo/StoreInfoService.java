package com.coremedia.livecontext.ecommerce.ibm.storeinfo;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.TimeZone;

public class StoreInfoService {

  private WcStoreInfoWrapperService wrapperService;
  private CommerceCache commerceCache;
  private int delayOnError;
  private StoreInfoCacheKey storeInfoCacheKey;

  public String getStoreId(String storeName) {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    return DataMapHelper.findStringValue(storeInfos, "stores." + storeName + ".storeId").orElse(null);
  }

  public String getDefaultCatalogId(String storeName) {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    return DataMapHelper.findStringValue(storeInfos, "stores." + storeName + ".defaultCatalogId").orElse(null);
  }

  public String getDefaultCatalogName(String storeName) {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    return DataMapHelper.findStringValue(storeInfos, "stores." + storeName + ".defaultCatalog").orElse(null);
  }

  public String getCatalogId(String storeName, String catalogName) {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    return DataMapHelper.findStringValue(storeInfos, "stores." + storeName + ".catalogs." + catalogName).orElse(null);
  }

  public Map<String, Object> getStoreInfos() {
    return commerceCache.get(storeInfoCacheKey);
  }

  @Nonnull
  public TimeZone getTimeZone() {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    String sTimeZoneId = DataMapHelper.findStringValue(storeInfos, "serverTimezoneId").orElse(null);
    return TimeZone.getTimeZone(sTimeZoneId);
  }

  public String getWcsVersion() {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    return DataMapHelper.findStringValue(storeInfos, "wcsVersion").orElse(null);
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
    storeInfoCacheKey = new StoreInfoCacheKey(AbstractCommerceCacheKey.CONFIG_KEY_STORE_INFO, wrapperService, commerceCache, delayOnError);
  }
}
