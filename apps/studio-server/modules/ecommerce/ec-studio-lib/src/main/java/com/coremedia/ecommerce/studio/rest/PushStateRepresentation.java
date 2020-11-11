package com.coremedia.ecommerce.studio.rest;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Date;

/**
 * @deprecated This class is part of the "push" implementation that is not supported by the
 * Commerce Hub architecture. It will be removed or changed in the future.
 */
@Deprecated
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
