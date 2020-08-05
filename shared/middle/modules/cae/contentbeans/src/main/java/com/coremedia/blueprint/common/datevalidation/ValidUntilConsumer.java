package com.coremedia.blueprint.common.datevalidation;

import java.time.Instant;
import java.util.function.Consumer;

public interface ValidUntilConsumer extends Consumer<Instant> {
  String DISABLE_VALIDITY_RECORDING_ATTRIBUTE = ValidUntilConsumer.class.getName() + "disableRecording";
}
