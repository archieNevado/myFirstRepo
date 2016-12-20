package com.coremedia.livecontext.ecommerce.ibm.event;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.springframework.context.AbstractSmartLifecyle;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.collect.Sets.newHashSet;

class CommerceCacheInvalidationLifecycle extends AbstractSmartLifecyle {
  private static final Logger LOG = LoggerFactory.getLogger(CommerceCacheInvalidationLifecycle.class);

  @VisibleForTesting
  final Map<String, CacheInvalidatorRunnable> runnablesByEndpoint = new HashMap<>();

  @VisibleForTesting
  final CacheKey<Collection<CommerceConnection>> commerceConnectionsCacheKey = new CommerceConnectionsCacheKey(this);

  private final TaskScheduler taskScheduler;
  private final IbmCommerceConnections commerceConnections;
  private final CommerceCacheInvalidationListener commerceCacheInvalidationListener;
  private final Cache cache;
  private final long interval;
  private final long errorInterval;

  @SuppressWarnings("FieldAccessedSynchronizedAndUnsynchronized")
  private String serviceEndpoint;

  CommerceCacheInvalidationLifecycle(@Nonnull TaskScheduler taskScheduler,
                                     @Nonnull IbmCommerceConnections commerceConnections,
                                     @Nonnull CommerceCacheInvalidationListener commerceCacheInvalidationListener,
                                     @Nonnull Cache cache,
                                     long interval,
                                     long errorInterval) {
    // start late
    super(Integer.MAX_VALUE, true);
    this.taskScheduler = taskScheduler;
    this.commerceConnections = commerceConnections;
    this.commerceCacheInvalidationListener = commerceCacheInvalidationListener;
    this.cache = cache;
    this.interval = interval;
    this.errorInterval = errorInterval;
  }

  IbmCommerceConnections getCommerceConnections() {
    return commerceConnections;
  }

  @Override
  protected void doStart() {
    cache.get(commerceConnectionsCacheKey);
  }

  synchronized void manageRunnables(Collection<CommerceConnection> connections) {
    if (!isRunning()) {
      LOG.debug("Cache invalidations are disabled, ignoring connections {}", connections);
      return;
    }

    // maybe all of them are dead:
    Collection<String> deadEndpoints = newHashSet(runnablesByEndpoint.keySet());

    // create and start runners
    for (CommerceConnection connection : connections) {
      StoreContext storeContext = connection.getStoreContext();
      String endpoint = CommercePropertyHelper.replaceTokens(serviceEndpoint, storeContext);

      // this one is not dead
      deadEndpoints.remove(endpoint);

      // maybe it's new?
      if (!runnablesByEndpoint.containsKey(endpoint)) {
        CacheInvalidatorRunnable cacheInvalidatorRunnable = new CacheInvalidatorRunnable(
                taskScheduler,
                commerceCacheInvalidationListener,
                interval,
                errorInterval,
                connection);
        runnablesByEndpoint.put(endpoint, cacheInvalidatorRunnable);
        cacheInvalidatorRunnable.schedule();
        LOG.info("Registered runner for cache invalidations on {}", connection);
      }
    }

    // stop runners for dead endpoints
    for (String endpoint : deadEndpoints) {
      CacheInvalidatorRunnable runnable = runnablesByEndpoint.remove(endpoint);
      cancel(endpoint, runnable);
    }

    if (runnablesByEndpoint.isEmpty()) {
      LOG.warn("No endpoints for commerce cache invalidations found, make sure commerce contexts are properly configured.");
    } else {
      LOG.trace("Polling commerce cache invalidations on endpoints: {}", runnablesByEndpoint.keySet());
    }
  }

  @Override
  protected synchronized void doStop() {
    for (Map.Entry<String, CacheInvalidatorRunnable> entry : runnablesByEndpoint.entrySet()) {
      String key = entry.getKey();
      CacheInvalidatorRunnable runnable = entry.getValue();
      cancel(key, runnable);
    }
    runnablesByEndpoint.clear();
  }

  void cancel(String key, CacheInvalidatorRunnable runnable) {
    LOG.info("stopping CommerceCacheInvalidationListener for endpoint {}", key);
    runnable.cancel();
  }

  @Value("${livecontext.ibm.wcs.rest.url}")
  void setServiceEndpoint(String serviceEndpoint) {
    this.serviceEndpoint = serviceEndpoint; // NOSONAR
  }

}
