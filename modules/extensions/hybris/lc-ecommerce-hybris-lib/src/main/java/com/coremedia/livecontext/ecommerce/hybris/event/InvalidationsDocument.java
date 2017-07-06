package com.coremedia.livecontext.ecommerce.hybris.event;

import com.coremedia.livecontext.ecommerce.event.InvalidationEvent;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class InvalidationsDocument {

  @JsonProperty("lastInvalidation")
  private long lastInvalidation;

  private List<InvalidationEvent> invalidations;

  public long getLastInvalidation() {
    return lastInvalidation;
  }

  public List<InvalidationEvent> getInvalidations() {
    return invalidations;
  }
}
