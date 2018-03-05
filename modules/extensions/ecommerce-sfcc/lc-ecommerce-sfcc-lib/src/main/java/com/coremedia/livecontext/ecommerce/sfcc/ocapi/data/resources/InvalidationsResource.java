package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources;

import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;

/**
 * Invalidation resource.
 */
@Service("ocapiInvalidationsResource")
public class InvalidationsResource extends AbstractDataResource {

  private static final String CHANGE_EVENTS_PATH = "/change_events?timestamp=";

  @Nonnull
  public Map<String, Object> getInvalidations(long timestamp) {
    String path = CHANGE_EVENTS_PATH + timestamp;

    return getConnector().getResource(path, Map.class)
            .orElseGet(Collections::<String, Object>emptyMap);
  }
}
