package com.coremedia.livecontext.ecommerce.ibm.storeinfo;

import com.coremedia.cache.Cache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StoreInfoCacheKey extends AbstractCommerceCacheKey<Map<String, Object>> {

  private static final Logger LOG = LoggerFactory.getLogger(StoreInfoCacheKey.class);
  private static final long DELAY_ON_ERROR_SECONDS = 20;

  private WcStoreInfoWrapperService wrapperService;

  StoreInfoCacheKey(String id, WcStoreInfoWrapperService wrapperService, CommerceCache commerceCache) {
    super(id, null, CONFIG_KEY_STORE_INFO, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public Map<String, Object> computeValue(Cache cache) {
    Map<String, Object> storeInfos = wrapperService.getStoreInfos();
    if (storeInfos.isEmpty()) {
      LOG.warn("no store info provided by WCS, retrying in {} seconds", DELAY_ON_ERROR_SECONDS);
      Cache.cacheFor(DELAY_ON_ERROR_SECONDS, TimeUnit.SECONDS);
    }
    return storeInfos;
  }

  @Override
  public void addExplicitDependency(Map<String, Object> wcStoreInfos) {
    Cache.dependencyOn(AbstractCommerceCacheKey.CONFIG_KEY_STORE_INFO);
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey;
  }

}