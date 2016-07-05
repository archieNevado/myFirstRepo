package com.coremedia.livecontext.ecommerce.ibm.event;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.event.CommerceCacheInvalidation;
import com.coremedia.livecontext.ecommerce.event.CommerceCacheInvalidationPropagator;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper.getValueForKey;

/**
 * Service bean for polling invalidations from WCS.
 * All invalidation events are propagated to a list of {@link CommerceCacheInvalidationPropagator}
 */
class CommerceCacheInvalidationListener {
  static final String CONTENT_IDENTIFIER_PRODUCT = "ProductDisplay";
  static final String CONTENT_IDENTIFIER_CATEGORY = "CategoryDisplay";
  static final String CONTENT_IDENTIFIER_MARKETING_SPOT = "espot";
  static final String CONTENT_IDENTIFIER_SEGMENT = "segment";
  static final String CONTENT_IDENTIFIER_TOP_CATEGORY = "TopCategoryDisplay";

  long lastInvalidationTimestamp = -1;

  private final List<CommerceCacheInvalidationPropagator> cacheInvalidationPropagators;
  private final WcCacheWrapperService wcCacheWrapperService;

  @Autowired
  CommerceCacheInvalidationListener(@Nonnull List<CommerceCacheInvalidationPropagator> cacheInvalidationPropagators,
                                    @Nonnull WcCacheWrapperService wcCacheWrapperService) {
    this.cacheInvalidationPropagators = cacheInvalidationPropagators;
    this.wcCacheWrapperService = wcCacheWrapperService;
  }

  @Nonnull
  List<CommerceCacheInvalidation> pollCacheInvalidations(@Nonnull StoreContext storeContext) {
    if (lastInvalidationTimestamp <= 0) {
      lastInvalidationTimestamp = wcCacheWrapperService.getLatestTimestamp(storeContext);
      return Collections.emptyList();
    }

    Map<String, Object> cacheInvalidations = wcCacheWrapperService.getCacheInvalidations(lastInvalidationTimestamp, storeContext);
    lastInvalidationTimestamp = getValueForKey(cacheInvalidations, "lastInvalidation", Double.class).longValue();
    List<Map<String, Object>> invalidations = getValueForKey(cacheInvalidations, "invalidations", List.class);

    // the list to return using API classes
    List<CommerceCacheInvalidation> commerceCacheInvalidations = new ArrayList<>();
    if (invalidations != null && !invalidations.isEmpty()) {
      for (Map<String, Object> invalidation : invalidations) {
        commerceCacheInvalidations.add(convertEvent(invalidation));
      }
      return commerceCacheInvalidations;
    }
    return Collections.emptyList();
  }

  void invalidateCacheEntries(List<CommerceCacheInvalidation> remoteInvalidations) {
    for (CommerceCacheInvalidationPropagator propagator : cacheInvalidationPropagators) {
      propagator.invalidate(remoteInvalidations);
    }
  }

  CommerceCacheInvalidation convertEvent(Map<String, Object> event) {
    CommerceCacheInvalidationImpl cacheInvalidation = new CommerceCacheInvalidationImpl();
    if (event != null) {
      switch (getValueForKey(event, "contentType", String.class)) {
        case CONTENT_IDENTIFIER_PRODUCT:
          event.put("id", CommerceIdHelper.formatProductTechId(getValueForKey(event, "techId", String.class)));
          event.put("contentType", CommerceCacheInvalidationImpl.CONTENT_IDENTIFIER_PRODUCT);
          break;
        case CONTENT_IDENTIFIER_CATEGORY: // same as top category
        case CONTENT_IDENTIFIER_TOP_CATEGORY:
          event.put("id", CommerceIdHelper.formatCategoryTechId(getValueForKey(event, "techId", String.class)));
          event.put("contentType", CommerceCacheInvalidationImpl.CONTENT_IDENTIFIER_CATEGORY);
          break;
        case CONTENT_IDENTIFIER_MARKETING_SPOT:
          event.put("id", CommerceIdHelper.formatMarketingSpotId(getValueForKey(event, "name", String.class)));
          event.put("contentType", CommerceCacheInvalidationImpl.CONTENT_IDENTIFIER_MARKETING_SPOT);
          break;
        case CONTENT_IDENTIFIER_SEGMENT:
          event.put("id", CommerceIdHelper.formatSegmentId(getValueForKey(event, "techId", String.class)));
          event.put("contentType", CommerceCacheInvalidationImpl.CONTENT_IDENTIFIER_SEGMENT);
          break;
      }

      cacheInvalidation.putAll(event);
    }
    return cacheInvalidation;
  }

}
