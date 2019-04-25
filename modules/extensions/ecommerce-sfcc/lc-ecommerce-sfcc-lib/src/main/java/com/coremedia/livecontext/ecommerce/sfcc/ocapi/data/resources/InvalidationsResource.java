package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * Invalidation resource.
 */
@DefaultAnnotation(NonNull.class)
@Service("ocapiInvalidationsResource")
public class InvalidationsResource extends AbstractDataResource {

  private static final String CHANGE_EVENTS_PATH = "/change_events?timestamp=";

  public Map<String, Object> getInvalidations(long timestamp, StoreContext storeContext) {
    String path = CHANGE_EVENTS_PATH + timestamp;

    return getConnector()
            .getResource(path, Map.class, storeContext)
            .orElseGet(Collections::<String, Object>emptyMap);
  }
}
