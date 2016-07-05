package com.coremedia.livecontext.ecommerce.ibm.event;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.event.CommerceCacheInvalidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

class CacheInvalidatorRunnable implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(CacheInvalidatorRunnable.class);

  private static final String TECH_ID = "-1";

  private final CommerceCacheInvalidationListener cacheInvalidator;
  private final CommerceConnection commerceConnection;
  private final TaskScheduler taskScheduler;
  private final long interval;
  private final long errorInterval;

  private Exception error;
  private ScheduledFuture<?> scheduledFuture;

  CacheInvalidatorRunnable(@Nonnull TaskScheduler taskScheduler,
                           @Nonnull CommerceCacheInvalidationListener cacheInvalidator,
                           long interval,
                           long errorInterval,
                           @Nonnull CommerceConnection commerceConnection) {
    this.taskScheduler = taskScheduler;
    this.cacheInvalidator = cacheInvalidator;
    this.commerceConnection = commerceConnection;
    this.interval = interval;
    this.errorInterval = errorInterval;
  }

  @Override
  public void run() {
    try {
      Commerce.setCurrentConnection(commerceConnection.getClone());
      List<CommerceCacheInvalidation> invalidations = cacheInvalidator.pollCacheInvalidations(commerceConnection.getStoreContext());
      if (null != error) {
        // after former errors we now seem to work again...
        LOG.info("Recovered from error situation, polling for cache invalidation is working again. Invalidating all cached commerce data...");
        error = null;
        // now we are re-connected, better to clear cache to remove stale cached errors during unconnected state
        CommerceCacheInvalidationImpl syntheticClearAll = new CommerceCacheInvalidationImpl();
        syntheticClearAll.setContentType(CommerceCacheInvalidationImpl.EVENT_CLEAR_ALL_EVENT_ID);
        syntheticClearAll.setTechId(TECH_ID);

        invalidations = Collections.singletonList((CommerceCacheInvalidation) syntheticClearAll);
      }
      cacheInvalidator.invalidateCacheEntries(invalidations);

    } catch (Exception throwable) {
      this.error = throwable;
    } finally {
      Commerce.clearCurrent();
    }

    // schedule next execution of this thread...
    schedule();
  }

  /**
   * Schedule execution of this runnable. It's synchronized so that we can be sure that scheduledFuture is set when
   * returning from this call
   */
  synchronized void schedule() {
    ScheduledFuture<?> predecessor = scheduledFuture;
    if (null == scheduledFuture || !predecessor.isCancelled()) {
      long delay = interval;
      if (null != error) {
        delay = errorInterval;
        LOG.warn("Exception while polling commerce system, delaying next request for " + delay + " ms", error.getMessage());
      }
      scheduledFuture = taskScheduler.schedule(this, new Date(System.currentTimeMillis() + delay));
    }
  }

  synchronized void cancel() {
    scheduledFuture.cancel(true);
  }

  @Override
  public String toString() {
    return "CacheInvalidatorRunnable{" +
            "error=" + error +
            ", errorInterval=" + errorInterval +
            ", interval=" + interval +
            ", commerceConnection=" + commerceConnection +
            '}';
  }
}
