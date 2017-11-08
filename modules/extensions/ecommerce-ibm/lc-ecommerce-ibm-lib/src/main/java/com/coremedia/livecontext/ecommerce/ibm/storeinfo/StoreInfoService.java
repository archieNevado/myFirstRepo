package com.coremedia.livecontext.ecommerce.ibm.storeinfo;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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
    return DataMapHelper.getValueForPath(storeInfos, "stores." + storeName + ".storeId", String.class);
  }

  public String getDefaultCatalogId(String storeName) {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    return DataMapHelper.getValueForPath(storeInfos, "stores." + storeName + ".defaultCatalogId", String.class);
  }

  public String getDefaultCatalogName(String storeName) {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    return DataMapHelper.getValueForPath(storeInfos, "stores." + storeName + ".defaultCatalog", String.class);
  }

  public String getCatalogId(String storeName, String catalogName) {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    return DataMapHelper.getValueForPath(storeInfos, "stores." + storeName + ".catalogs." + catalogName, String.class);
  }

  public Map<String, Object> getStoreInfos() {
    return commerceCache.get(storeInfoCacheKey);
  }

  public TimeZone getTimeZone() {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    String sTimeZoneId = DataMapHelper.getValueForPath(storeInfos, "serverTimezoneId", String.class);
    return TimeZone.getTimeZone(sTimeZoneId);
  }

  public String getWcsVersion() {
    Map<String, Object> storeInfos = commerceCache.get(storeInfoCacheKey);
    return DataMapHelper.getValueForPath(storeInfos, "wcsVersion", String.class);
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
