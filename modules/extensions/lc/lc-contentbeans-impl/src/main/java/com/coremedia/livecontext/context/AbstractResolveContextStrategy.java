package com.coremedia.livecontext.context;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

public abstract class AbstractResolveContextStrategy implements ResolveContextStrategy {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractResolveContextStrategy.class);

  private static final long DEFAULT_CACHED_IN_SECONDS = 24 * 60 * 60L;

  private LiveContextNavigationFactory liveContextNavigationFactory;
  private long cachedInSeconds = DEFAULT_CACHED_IN_SECONDS;
  private Cache cache;

  @Nullable
  protected abstract Category findNearestCategoryFor(@Nonnull String externalDescriptor, @Nonnull StoreContext storeContext);

  @Required
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  protected CatalogService getCatalogService() {
    return getCommerceConnection().getCatalogService();
  }

  protected StoreContextProvider getStoreContextProvider() {
    return getCommerceConnection().getStoreContextProvider();
  }

  @Nonnull
  protected CommerceConnection getCommerceConnection() {
    return requireNonNull(DefaultConnection.get(), "no commerce connection available");
  }

  @Required
  public void setLiveContextNavigationFactory(LiveContextNavigationFactory liveContextNavigationFactory) {
    this.liveContextNavigationFactory = liveContextNavigationFactory;
  }

  public void setCachedInSeconds(long cachedInSeconds) {
    this.cachedInSeconds = cachedInSeconds;
  }

  @Nullable
  @Override
  public LiveContextNavigation resolveContext(@Nonnull Site site, @Nonnull String externalDescriptor) {
    notNull(site);
    hasText(externalDescriptor);
    // cache is required for performance concerns in production use,
    // functionally it also works without. Maybe useful for testing.
    return cache!=null ? cache.get(new CommerceContextProviderCacheKey(site, externalDescriptor)) : resolveContextUncached(site, externalDescriptor);
  }

  @Nullable
  private LiveContextNavigation resolveContextUncached(Site site, String externalDescriptor) {
    Cache.cacheFor(cachedInSeconds, TimeUnit.SECONDS);
    StoreContext storeContext = getStoreContextProvider().findContextBySite(site);

    if (storeContext == null) {
      LOG.warn("Could not find a store context for {}", site.getName());
      return null;
    }

    Category category = findNearestCategoryFor(externalDescriptor, storeContext);
    if (category == null) {
      LOG.warn("Could not find a category for external descriptor {}", externalDescriptor);
      return null;
    }

    return liveContextNavigationFactory.createNavigation(category, site);
  }


  // --- inner classes ----------------------------------------------

  private class CommerceContextProviderCacheKey extends CacheKey<LiveContextNavigation> {
    private final String externalDescriptor;
    private final Site site;

    CommerceContextProviderCacheKey(@Nonnull Site site, @Nonnull String externalDescriptor) {
      this.site = site;
      this.externalDescriptor = externalDescriptor;
    }

    @Override
    public LiveContextNavigation evaluate(Cache cache) {
      return resolveContextUncached(site, externalDescriptor);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      CommerceContextProviderCacheKey that = (CommerceContextProviderCacheKey) o;
      return externalDescriptor.equals(that.externalDescriptor) && site.equals(that.site);
    }

    @Override
    public int hashCode() {
      int result = externalDescriptor.hashCode();
      result = 31 * result + site.hashCode();
      return result;
    }
  }
}
