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
  private final int delayOnError;

  private WcStoreInfoWrapperService wrapperService;

  StoreInfoCacheKey(String id,
                    WcStoreInfoWrapperService wrapperService,
                    CommerceCache commerceCache, int delayOnError) {
    super(id, null, CONFIG_KEY_STORE_INFO, commerceCache);
    this.wrapperService = wrapperService;
    this.delayOnError = delayOnError;
  }

  @Override
  public Map<String, Object> computeValue(Cache cache) {
    Map<String, Object> storeInfos = wrapperService.getStoreInfos();
    if (storeInfos.isEmpty()) {
      LOG.warn("no store info provided by WCS, retrying in {} seconds", delayOnError);
      Cache.cacheFor(delayOnError, TimeUnit.SECONDS);
    }
    return storeInfos;
  }

  @Override
  public void addExplicitDependency(Map<String, Object> storeInfos) {
    if (storeInfos.isEmpty()) {
      LOG.warn("no store info provided by WCS, retrying in {} seconds", delayOnError);
      Cache.cacheFor(delayOnError, TimeUnit.SECONDS);
    }
    Cache.dependencyOn(AbstractCommerceCacheKey.CONFIG_KEY_STORE_INFO);
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey;
  }

}