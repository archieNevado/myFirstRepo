package com.coremedia.ecommerce.studio.rest;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

public class PushStateRepresentation {

  private final String state;
  private final Date modificationDate;

  PushStateRepresentation(@NonNull String state, @Nullable Date modificationDate) {
    this.state = state;
    this.modificationDate = modificationDate;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getState() {
    return state;
  }

  public Date getModificationDate() {
    return modificationDate;
  }
}
