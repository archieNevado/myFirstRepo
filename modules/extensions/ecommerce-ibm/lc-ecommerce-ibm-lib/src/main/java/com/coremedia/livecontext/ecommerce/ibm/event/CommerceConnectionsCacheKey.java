package com.coremedia.livecontext.ecommerce.ibm.event;

import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;

class CommerceConnectionsCacheKey extends CacheKey<Collection<CommerceConnection>> {

  private final CommerceCacheInvalidationLifecycle commerceCacheInvalidationLifecycle;

  public CommerceConnectionsCacheKey(@Nonnull CommerceCacheInvalidationLifecycle commerceCacheInvalidationLifecycle) {
    this.commerceCacheInvalidationLifecycle = commerceCacheInvalidationLifecycle;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommerceConnectionsCacheKey that = (CommerceConnectionsCacheKey) o;
    return Objects.equals(commerceCacheInvalidationLifecycle, that.commerceCacheInvalidationLifecycle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(commerceCacheInvalidationLifecycle);
  }

  @Override
  public Collection<CommerceConnection> evaluate(Cache cache) throws Exception {
    return commerceCacheInvalidationLifecycle.getCommerceConnections().getConnections();
  }

  @Override
  public void updated(Cache cache, Collection<CommerceConnection> oldValue, Collection<CommerceConnection> value, boolean isEqual) {
    commerceCacheInvalidationLifecycle.manageRunnables(value);
  }

  @Override
  public void inserted(Cache cache, Collection<CommerceConnection> value) {
    commerceCacheInvalidationLifecycle.manageRunnables(value);
  }

  @Override
  public String cacheClass(Cache cache, Collection<CommerceConnection> value) {
    return CACHE_CLASS_ALWAYS_STAY_IN_CACHE;
  }
}
