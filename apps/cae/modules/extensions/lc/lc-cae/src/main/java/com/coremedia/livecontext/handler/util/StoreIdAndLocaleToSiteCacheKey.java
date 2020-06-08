package com.coremedia.livecontext.handler.util;

import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.cap.multisite.Site;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@DefaultAnnotation(NonNull.class)
public class StoreIdAndLocaleToSiteCacheKey extends CacheKey<Optional<Site>> {

  private final String storeId;
  private final Locale locale;
  private final LiveContextSiteResolverImpl liveContextSiteResolver;

  public StoreIdAndLocaleToSiteCacheKey(String storeId, Locale locale, LiveContextSiteResolverImpl liveContextSiteResolver) {
    this.storeId = storeId;
    this.locale = locale;
    this.liveContextSiteResolver = liveContextSiteResolver;
  }

  @Override
  public Optional<Site> evaluate(Cache cache) {
    return liveContextSiteResolver.findSiteForUncached(storeId, locale);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StoreIdAndLocaleToSiteCacheKey that = (StoreIdAndLocaleToSiteCacheKey) o;
    return storeId.equals(that.storeId) &&
            locale.equals(that.locale) &&
            liveContextSiteResolver.equals(that.liveContextSiteResolver);
  }

  @Override
  public int hashCode() {
    return Objects.hash(storeId, locale, liveContextSiteResolver);
  }
}
