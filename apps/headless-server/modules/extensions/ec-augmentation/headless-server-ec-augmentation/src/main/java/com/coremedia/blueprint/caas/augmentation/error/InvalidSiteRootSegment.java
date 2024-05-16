package com.coremedia.blueprint.caas.augmentation.error;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.ArrayList;
import java.util.List;

@DefaultAnnotation(NonNull.class)
public class InvalidSiteRootSegment implements GraphQLError {

  public static final String ERROR_MSG = "Invalid Site Root Segment";

  private static final InvalidSiteRootSegment instance = new InvalidSiteRootSegment();

  private InvalidSiteRootSegment() {
  }

  public static InvalidSiteRootSegment getInstance() {
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
