package com.coremedia.blueprint.caas.augmentation.error;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.ArrayList;
import java.util.List;

@DefaultAnnotation(NonNull.class)
public class InvalidCommerceId implements GraphQLError {

  public static final String ERROR_MSG = "Invalid Commerce ID";

  private static final InvalidCommerceId instance = new InvalidCommerceId();

  private InvalidCommerceId() {
  }

  public static InvalidCommerceId getInstance() {
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
