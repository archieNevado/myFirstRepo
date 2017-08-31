package com.coremedia.livecontext.ecommerce.ibm.storeinfo;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestServiceMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import javax.annotation.Nonnull;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

/**
 * A service that uses the getRestConnector() to get all store infos in wcs.
 */
class WcStoreInfoWrapperService extends AbstractWcWrapperService {

  private static final Logger LOG = LoggerFactory.getLogger(WcStoreInfoWrapperService.class);

  private static final WcRestServiceMethod<Map, Void>
          GET_STORE_INFO = WcRestConnector.createServiceMethod(HttpMethod.GET, "coremedia/storeinfo", false, false, Map.class);

  @Nonnull
  Map<String, Object> getStoreInfos() {
    //noinspection unchecked
    try {
      Map map = getRestConnector().callServiceInternal(GET_STORE_INFO, emptyList(), emptyMap(), null, null, null);
      return map != null ? map : emptyMap();
    } catch (CommerceException e) {
      LOG.warn("ignoring exception while retrieving WCS storeinfo: {}", e.getMessage());
      return emptyMap();
    }
  }
}
