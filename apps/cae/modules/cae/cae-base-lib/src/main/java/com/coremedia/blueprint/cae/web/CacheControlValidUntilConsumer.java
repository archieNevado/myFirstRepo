package com.coremedia.blueprint.cae.web;

import com.coremedia.blueprint.common.datevalidation.ValidUntilConsumer;
import com.coremedia.blueprint.common.util.ContextAttributes;
import com.coremedia.objectserver.web.cachecontrol.CacheControlStrategy;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.time.Instant;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Record a validity change instant in a {@link CacheControlStrategy} instance.
 */
@DefaultAnnotation(NonNull.class)
public class CacheControlValidUntilConsumer implements ValidUntilConsumer {

  private static final String DISABLE_VALIDITY_RECORDING_ATTRIBUTE = lookup().lookupClass().getName() + "#disableRecording";

  private final CacheControlStrategy<Instant> cacheControlStrategy;

  public CacheControlValidUntilConsumer(CacheControlStrategy<Instant> cacheControlStrategy) {
    this.cacheControlStrategy = cacheControlStrategy;
  }

  public static void disableRecording() {
    ContextAttributes.findRequest()
            .ifPresent(request -> request.setAttribute(DISABLE_VALIDITY_RECORDING_ATTRIBUTE, true));
  }

  public static void enableRecording() {
    ContextAttributes.findRequest()
            .ifPresent(request -> request.removeAttribute(DISABLE_VALIDITY_RECORDING_ATTRIBUTE));
  }

  @Override
  public void accept(Instant instant) {
    var request = ContextAttributes.findRequest().orElse(null);
    if (request == null || request.getAttribute(DISABLE_VALIDITY_RECORDING_ATTRIBUTE) != null) {
      return;
    }
    cacheControlStrategy.recordValidUntil(instant);
  }
}
