package com.coremedia.livecontext.ecommerce.sfcc.push;

import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

class GetStoredJsonCacheKey extends CacheKey<String> {
  private static final int CACHE_FOR = 60;
  private static final Logger LOG = LoggerFactory.getLogger(GetStoredJsonCacheKey.class);

  private final String pageKey;
  private final String customDependency;
  private final SfccContentHelper sfccContentHelper;
  private final StoreContext storeContext;

  GetStoredJsonCacheKey(String pageKey, StoreContext storeContext, SfccContentHelper sfccContentHelper, String customDependency) {
    this.pageKey = pageKey;
    this.storeContext = storeContext;
    this.sfccContentHelper = sfccContentHelper;
    this.customDependency = customDependency;
  }

  @Override
  public String evaluate(Cache cache) {
    String result;
    try {
      Cache.disableDependencies();
      result = sfccContentHelper.getStoredJsonByPageKey(pageKey, storeContext);
    } finally {
      Cache.enableDependencies();
    }
    LOG.debug("Caching for push status for {} {} s", pageKey, CACHE_FOR);
    Cache.cacheFor(CACHE_FOR, TimeUnit.SECONDS);
    Cache.dependencyOn(customDependency);

    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetStoredJsonCacheKey that = (GetStoredJsonCacheKey) o;
    return Objects.equals(pageKey, that.pageKey) &&
            Objects.equals(sfccContentHelper, that.sfccContentHelper) &&
            Objects.equals(storeContext, that.storeContext);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pageKey, sfccContentHelper, storeContext);
  }
}
