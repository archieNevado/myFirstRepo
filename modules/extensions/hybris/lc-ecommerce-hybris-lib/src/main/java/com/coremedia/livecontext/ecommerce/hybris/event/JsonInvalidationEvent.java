package com.coremedia.livecontext.ecommerce.hybris.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

class JsonInvalidationEvent {

  private final String techId;
  private final String contentType;
  private final long timestamp;

  @JsonCreator
  JsonInvalidationEvent(@JsonProperty("techId") String techId,
                        @JsonProperty("contentType") String contentType,
                        @JsonProperty("timestamp") long timestamp) {
    this.techId = techId;
    this.contentType = contentType;
    this.timestamp = timestamp;
  }

  String getTechId() {
    return techId;
  }

  String getContentType() {
    return contentType;
  }

  long getTimestamp() {
    return timestamp;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
            .add("techId", techId)
            .add("contentType", contentType)
            .add("timestamp", timestamp)
            .toString();
  }
}
