package com.coremedia.blueprint.common.datevalidation;

import java.time.Instant;
import java.util.function.Consumer;

/**
 * Consume an instant as defined by {@link ValidityPeriod}. This is useful to record upcoming
 * changes of the contents currently being served or rendered.
 */
public interface ValidUntilConsumer extends Consumer<Instant> {

}
