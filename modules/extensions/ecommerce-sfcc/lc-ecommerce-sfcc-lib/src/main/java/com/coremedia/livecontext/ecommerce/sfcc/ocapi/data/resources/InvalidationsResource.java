package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources;

import org.springframework.stereotype.Service;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Collections;
import java.util.Map;

/**
 * Invalidation resource.
 */
@Service("ocapiInvalidationsResource")
public class InvalidationsResource extends AbstractDataResource {

  private static final String CHANGE_EVENTS_PATH = "/change_events?timestamp=";

  @NonNull
  public Map<String, Object> getInvalidations(long timestamp) {
    String path = CHANGE_EVENTS_PATH + timestamp;

    return getConnector().getResource(path, Map.class)
            .orElseGet(Collections::<String, Object>emptyMap);
  }
}
