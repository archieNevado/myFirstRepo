package com.coremedia.livecontext.ecommerce.ibm.event;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestServiceMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * A service that uses the getRestConnector() to poll invalidation events.
 */
class WcInvalidationWrapperService extends AbstractWcWrapperService {
  private static final Logger LOG = LoggerFactory.getLogger(WcInvalidationWrapperService.class);

  private static final WcRestServiceMethod<Map, Void>
          GET_CACHE_INVALIDATION = WcRestConnector.createServiceMethod(HttpMethod.GET, "coremedia/cacheinvalidation/{startTimestamp}?maxWait={maxWait}&chunkSize={chunkSize}", false, false, Map.class);

  @Nonnull
  Map<String, Object> getCacheInvalidations(long lastExecutionTimeStamp,
                                            long maxWaitInMilliseconds,
                                            long chunkSize,
                                            @Nonnull StoreContext storeContext) throws CommerceException {
    //noinspection unchecked
    Map<String, Object> wcCommerceCacheInvalidations = getRestConnector().callService(
            GET_CACHE_INVALIDATION, asList(String.valueOf(lastExecutionTimeStamp), String.valueOf(maxWaitInMilliseconds), String.valueOf(chunkSize)),
            Collections.emptyMap(), null, storeContext, null);
    if (wcCommerceCacheInvalidations == null) {
      LOG.warn("Could not poll cache invalidations from commerce system");
      throw new CommerceException("Could not poll cache invalidations from commerce system");
    }
    return wcCommerceCacheInvalidations;
  }
}
