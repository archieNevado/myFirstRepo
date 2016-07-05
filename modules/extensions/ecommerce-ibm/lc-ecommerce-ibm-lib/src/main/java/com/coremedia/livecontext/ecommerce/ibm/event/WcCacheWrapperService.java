package com.coremedia.livecontext.ecommerce.ibm.event;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
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
class WcCacheWrapperService extends AbstractWcWrapperService {
  private static final Logger LOG = LoggerFactory.getLogger(WcCacheWrapperService.class);

  private static final WcRestConnector.WcRestServiceMethod<Map, Void>
          GET_CACHE_INVALIDATION = WcRestConnector.createServiceMethod(HttpMethod.GET, "coremedia/cacheinvalidation/{startTimestamp}?maxWait={maxWait}&chunkSize={chunkSize}", false, false, Map.class);

  private static final WcRestConnector.WcRestServiceMethod<Map, Void>
          GET_LATEST_TIMESTAMP = WcRestConnector.createServiceMethod(HttpMethod.GET, "coremedia/cacheinvalidation/latestTimestamp?maxWait={maxWait}", false, false, Map.class);
  private static final int CHUNK_SIZE = 5000;
  private static final int MAX_POLL_WAIT = 20000; //

  @Nonnull
  Map<String, Object> getCacheInvalidations(long lastExecutionTimeStamp, @Nonnull StoreContext storeContext) throws CommerceException {
    //noinspection unchecked
    Map<String, Object> wcCommerceCacheInvalidations = getRestConnector().callService(GET_CACHE_INVALIDATION, asList(String.valueOf(lastExecutionTimeStamp), String.valueOf(MAX_POLL_WAIT), String.valueOf(CHUNK_SIZE)), null, null, storeContext, null);
    if (wcCommerceCacheInvalidations == null) {
      LOG.warn("Could not poll cache invalidations from commerce system");
      throw new CommerceException("Could not poll cache invalidations from commerce system");
    }
    return wcCommerceCacheInvalidations;
  }

  long getLatestTimestamp(@Nonnull StoreContext storeContext) throws CommerceException {
    long result = -1;
    Map map = getRestConnector().callService(GET_LATEST_TIMESTAMP, Collections.singletonList(String.valueOf(MAX_POLL_WAIT)), null, null, storeContext, null);
    if (map == null) {
      //404 and 204
      LOG.warn("Could not request cache latest invalidation timestamp from commerce system");
      throw new CommerceException("Could not request latest cache invalidation timestamp from commerce system");
    } else if (map.containsKey("timestamp")) {
      String tsStr = (String) map.get("timestamp");
      result = Long.valueOf(tsStr);
    }
    return result;
  }
}
