package com.coremedia.blueprint.caas.augmentation.error;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.ArrayList;
import java.util.List;

@DefaultAnnotation(NonNull.class)
public class InvalidSiteId implements GraphQLError {

  public static final String ERROR_MSG = "Invalid Site ID";

  private static final InvalidSiteId instance = new InvalidSiteId();

  private InvalidSiteId() {
  }

  public static InvalidSiteId getInstance() {
    return instance;
  }

  @Override
  public String getMessage() {
    return ERROR_MSG;
  }

  @Override
  public List<SourceLocation> getLocations() {
    return new ArrayList<>();
  }

  @Override
  public ErrorType getErrorType() {
    return ErrorType.DataFetchingException;
  }

}
