package com.coremedia.livecontext.ecommerce.ibm.event;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.event.InvalidationEvent;
import com.coremedia.livecontext.ecommerce.event.InvalidationPropagator;
import com.coremedia.livecontext.ecommerce.event.InvalidationService;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.event.InvalidationEvent.CATEGORY_EVENT;
import static com.coremedia.livecontext.ecommerce.event.InvalidationEvent.CLEAR_ALL_EVENT;
import static com.coremedia.livecontext.ecommerce.event.InvalidationEvent.MARKETING_SPOT_EVENT;
import static com.coremedia.livecontext.ecommerce.event.InvalidationEvent.PRODUCT_EVENT;
import static com.coremedia.livecontext.ecommerce.event.InvalidationEvent.SEGMENT_EVENT;
import static com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper.getValueForKey;

/**
 * Service bean for polling invalidations from WCS.
 * All invalidation events are propagated to a list of {@link InvalidationPropagator}
 */
class InvalidationServiceImpl implements InvalidationService {

  private static final Logger LOG = LoggerFactory.getLogger(InvalidationServiceImpl.class);

  private static final String CONTENT_IDENTIFIER_CLEAR_ALL = "clearall";
  static final String CONTENT_IDENTIFIER_PRODUCT = "ProductDisplay";
  static final String CONTENT_IDENTIFIER_CATEGORY = "CategoryDisplay";
  static final String CONTENT_IDENTIFIER_MARKETING_SPOT = "espot";
  static final String CONTENT_IDENTIFIER_SEGMENT = "segment";
  static final String CONTENT_IDENTIFIER_TOP_CATEGORY = "TopCategoryDisplay";

  private final WcInvalidationWrapperService wcInvalidationWrapperService;

  private long maxWaitInMilliseconds;

  private long chunkSize;

  InvalidationServiceImpl(@Nonnull WcInvalidationWrapperService wcInvalidationWrapperService,
                          long maxWaitInMilliseconds, long chunkSize) {
    this.wcInvalidationWrapperService = wcInvalidationWrapperService;
    this.maxWaitInMilliseconds = maxWaitInMilliseconds;
    this.chunkSize = chunkSize;
  }

  @Nonnull
  public List<InvalidationEvent> getInvalidations(long timestamp) throws CommerceException {

    StoreContext storeContext = StoreContextHelper.getCurrentContextOrThrow();
    // the invalidation thread would never be healthy again if a network error had occurred previously
    StoreContextHelper.setCommerceSystemIsUnavailable(storeContext, false);

    // in some cases (when the commerce system wasn't available in the initial run) the store context
    // can be incomplete (missing values because they couldn't be resolved via StoreInfoHandler)
    if (!StoreContextHelper.isValid(storeContext)) {
      storeContext = restoreStoreContext(storeContext);
    }

    Map<String, Object> cacheInvalidations = wcInvalidationWrapperService.getCacheInvalidations(
            timestamp, maxWaitInMilliseconds, chunkSize, storeContext);
    List<Map<String, Object>> invalidations = getValueForKey(cacheInvalidations, "invalidations", List.class);
    long lastInvalidationTimestamp = getValueForKey(cacheInvalidations, "lastInvalidation", -1L);
    if (invalidations != null && !invalidations.isEmpty()) {
      // the list to return using API classes
      List<InvalidationEvent> commerceCacheInvalidations = new ArrayList<>();
      for (Map<String, Object> invalidation : invalidations) {
        commerceCacheInvalidations.add(convertEvent(invalidation, lastInvalidationTimestamp));
      }
      return commerceCacheInvalidations;
    }
    return Collections.emptyList();
  }

  private StoreContext restoreStoreContext(StoreContext storeContext) {
    String siteId = storeContext.getSiteId();
    LOG.debug("Invalid store context found for site: {}, trying to restore...", siteId);
    try {
      CommerceConnection commerceConnection = DefaultConnection.get();
      if (commerceConnection != null) {
        StoreContextProvider storeContextProvider = commerceConnection.getStoreContextProvider();
        StoreContext storeContextNew = storeContextProvider.findContextBySiteId(siteId);
        StoreContextHelper.setCurrentContext(storeContextNew);
        LOG.debug("Store context restored: {}", storeContextNew);
        return storeContextNew;
      }
      else {
        LOG.warn("Store context restore was not successful, commerce connection is null for site {}", siteId);
      }
    } catch (InvalidContextException e) {
      LOG.debug("Restore was not successful: {}", e.getMessage());
    }
    return storeContext;
  }

  @Nonnull
  @Override
  public String getServiceEndpointId() {
    String serviceEndpointId = null;
    if (wcInvalidationWrapperService.getRestConnector() != null) {
      serviceEndpointId = wcInvalidationWrapperService.getRestConnector().getServiceEndpoint(StoreContextHelper.getCurrentContext());
    }
    return serviceEndpointId != null ? serviceEndpointId : "unknown";
  }

  InvalidationEvent convertEvent(Map<String, Object> event, long lastTimestamp) {
    if (event != null) {
      String timestampStr = getValueForKey(event, "timestamp", String.class);
      long timestamp = lastTimestamp;
      if (timestampStr != null) {
        timestamp = Long.parseLong(timestampStr);
      }
      String contentType = getValueForKey(event, "contentType", String.class);
      if (contentType != null) {
        switch (contentType) {
          case CONTENT_IDENTIFIER_CLEAR_ALL:
            return new InvalidationEvent(
                    getValueForKey(event, "techId", String.class),
                    getValueForKey(event, "techId", String.class),
                    getValueForKey(event, "name", String.class),
                    CLEAR_ALL_EVENT, timestamp
            );
          case CONTENT_IDENTIFIER_PRODUCT:
            return new InvalidationEvent(
                    CommerceIdHelper.formatProductTechId(getValueForKey(event, "techId", String.class)),
                    getValueForKey(event, "techId", String.class),
                    getValueForKey(event, "name", String.class),
                    PRODUCT_EVENT, timestamp
            );
          case CONTENT_IDENTIFIER_CATEGORY: // same as top category
          case CONTENT_IDENTIFIER_TOP_CATEGORY:
            return new InvalidationEvent(
                    CommerceIdHelper.formatCategoryTechId(getValueForKey(event, "techId", String.class)),
                    getValueForKey(event, "techId", String.class),
                    getValueForKey(event, "name", String.class),
                    CATEGORY_EVENT, timestamp
            );
          case CONTENT_IDENTIFIER_MARKETING_SPOT:
            return new InvalidationEvent(
                    CommerceIdHelper.formatMarketingSpotId(getValueForKey(event, "name", String.class)),
                    getValueForKey(event, "name", String.class),
                    getValueForKey(event, "name", String.class),
                    MARKETING_SPOT_EVENT, timestamp
            );
          case CONTENT_IDENTIFIER_SEGMENT:
            return new InvalidationEvent(
                    CommerceIdHelper.formatSegmentId(getValueForKey(event, "techId", String.class)),
                    getValueForKey(event, "techId", String.class),
                    getValueForKey(event, "name", String.class),
                    SEGMENT_EVENT, timestamp
            );
        }
      }
      return new InvalidationEvent(
              getValueForKey(event, "techId", String.class),
              getValueForKey(event, "techId", String.class),
              getValueForKey(event, "name", String.class),
              null, timestamp
      );
    }
    return null;
  }

}
