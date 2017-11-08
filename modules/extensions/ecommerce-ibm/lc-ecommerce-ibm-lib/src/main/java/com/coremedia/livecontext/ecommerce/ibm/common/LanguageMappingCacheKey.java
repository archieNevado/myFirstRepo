package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;

import javax.annotation.Nonnull;
import java.util.Map;

class LanguageMappingCacheKey extends CacheKey<Map<String, String>> {

  @Nonnull
  private WcLanguageMappingService wcLanguageMappingService;

  LanguageMappingCacheKey(@Nonnull WcLanguageMappingService wcLanguageMappingService) {
    this.wcLanguageMappingService = wcLanguageMappingService;
  }

  @Override
  public Map<String, String> evaluate(Cache cache) throws Exception {
    return wcLanguageMappingService.getLanguageMappingUncached();
  }

  @Override
  public String cacheClass(Cache cache, Map<String, String> value) {
    return Cache.CACHE_CLASS_ALWAYS_STAY_IN_CACHE;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    LanguageMappingCacheKey that = (LanguageMappingCacheKey) o;

    return wcLanguageMappingService.equals(that.wcLanguageMappingService);
  }

  @Override
  public int hashCode() {
    return wcLanguageMappingService.hashCode();
  }
}
