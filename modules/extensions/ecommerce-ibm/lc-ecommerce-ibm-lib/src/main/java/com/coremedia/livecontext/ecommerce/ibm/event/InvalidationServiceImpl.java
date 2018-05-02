package com.coremedia.livecontext.ecommerce.ibm.event;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.event.InvalidationEvent;
import com.coremedia.livecontext.ecommerce.event.InvalidationPropagator;
import com.coremedia.livecontext.ecommerce.event.InvalidationService;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmService;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.coremedia.livecontext.ecommerce.event.InvalidationEvent.CATEGORY_EVENT;
import static com.coremedia.livecontext.ecommerce.event.InvalidationEvent.CLEAR_ALL_EVENT;
import static com.coremedia.livecontext.ecommerce.event.InvalidationEvent.MARKETING_SPOT_EVENT;
import static com.coremedia.livecontext.ecommerce.event.InvalidationEvent.PRODUCT_EVENT;
import static com.coremedia.livecontext.ecommerce.event.InvalidationEvent.SEGMENT_EVENT;
import static com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper.findStringValue;
import static com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper.findValue;
import static com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper.getListValue;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * Service bean for polling invalidations from WCS.
 * All invalidation events are propagated to a list of {@link InvalidationPropagator}
 */
class InvalidationServiceImpl extends AbstractIbmService implements InvalidationService {

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

  @Override
  @Nonnull
  public List<InvalidationEvent> getInvalidations(long timestamp, @Nonnull StoreContext storeContext) {
    // the invalidation thread would never be healthy again if a network error had occurred previously
    StoreContextHelper.setCommerceSystemIsUnavailable(storeContext, false);

    // in some cases (when the commerce system wasn't available in the initial run) the store context
    // can be incomplete (missing values because they couldn't be resolved via StoreInfoHandler)
    if (!StoreContextHelper.isValid(storeContext)) {
      storeContext = restoreStoreContext(storeContext);
    }

    Map<String, Object> cacheInvalidations = wcInvalidationWrapperService.getCacheInvalidations(
            timestamp, maxWaitInMilliseconds, chunkSize, storeContext);
    List<Map<String, Object>> invalidations = getListValue(cacheInvalidations, "invalidations");
    if (invalidations.isEmpty()) {
      return emptyList();
    }

    long lastInvalidationTimestamp = findValue(cacheInvalidations, "lastInvalidation", Long.class).orElse(-1L);

    // the list to return using API classes
    return invalidations.stream()
            .filter(Objects::nonNull)
            .map(invalidation -> convertEvent(invalidation, lastInvalidationTimestamp))
            .collect(toList());
  }

  @Nonnull
  private static StoreContext restoreStoreContext(@Nonnull StoreContext storeContext) {
    String siteId = storeContext.getSiteId();
    LOG.debug("Invalid store context found for site: {}, trying to restore...", siteId);
    try {
      CommerceConnection commerceConnection = CurrentCommerceConnection.find().orElse(null);
      if (commerceConnection != null) {
        StoreContextProvider storeContextProvider = commerceConnection.getStoreContextProvider();
        StoreContext storeContextNew = storeContextProvider.findContextBySiteId(siteId);
        StoreContextHelper.setCurrentContext(storeContextNew);
        LOG.debug("Store context restored: {}", storeContextNew);

        // Store context won't be `null` here as `StoreContextHelper.setCurrentContext`
        // would have raised an exception.
        //noinspection ConstantConditions
        return storeContextNew;
      } else {
        LOG.warn("Store context restore was not successful, commerce connection is null for site {}", siteId);
      }
    } catch (InvalidContextException e) {
      LOG.debug("Restore was not successful: {}", e.getMessage());
    }
    return storeContext;
  }

  @Nonnull
  @Override
  public String getServiceEndpointId(@Nonnull StoreContext storeContext) {
    return Optional.ofNullable(wcInvalidationWrapperService.getRestConnector())
            .map(restConnector -> restConnector.getServiceEndpoint(storeContext))
            .orElse("unknown");
  }

  @Nonnull
  InvalidationEvent convertEvent(@Nonnull Map<String, Object> event, long lastTimestamp) {
    long timestamp = findStringValue(event, "timestamp")
            .map(Long::parseLong)
            .orElse(lastTimestamp);

    String contentType = getStringValueForKey(event, "contentType");
    if (contentType != null) {
      switch (contentType) {
        case CONTENT_IDENTIFIER_CLEAR_ALL:
          return new InvalidationEvent(getStringValueForKey(event, "techId"), CLEAR_ALL_EVENT, timestamp);
        case CONTENT_IDENTIFIER_PRODUCT:
          return new InvalidationEvent(getStringValueForKey(event, "techId"), PRODUCT_EVENT, timestamp);
        case CONTENT_IDENTIFIER_CATEGORY: // same as top category
        case CONTENT_IDENTIFIER_TOP_CATEGORY:
          return new InvalidationEvent(getStringValueForKey(event, "techId"), CATEGORY_EVENT, timestamp);
        case CONTENT_IDENTIFIER_MARKETING_SPOT:
          return new InvalidationEvent(getStringValueForKey(event, "techId"), MARKETING_SPOT_EVENT, timestamp);
        case CONTENT_IDENTIFIER_SEGMENT:
          return new InvalidationEvent(getStringValueForKey(event, "techId"), SEGMENT_EVENT, timestamp);
      }
    }

    return new InvalidationEvent(getStringValueForKey(event, "techId"), null, timestamp);
  }

  @Nullable
  private static String getStringValueForKey(@Nonnull Map<String, Object> map, @Nonnull String key) {
    return findStringValue(map, key).orElse(null);
  }
}
