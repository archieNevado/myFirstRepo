package com.coremedia.blueprint.cae.view;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * A wrapper class for bean includes
 */
public class HashBasedFragmentHandler extends DynamicInclude {

  public static final String MODIFIED_PARAMETERS_HEADER_PREFIX = "CM_MODIFIED_PARAMETERS_";

  private List<String> validParameters;

  public HashBasedFragmentHandler(Object delegate, String view, List<String> validParameters) {
    super(delegate, view);
    this.validParameters = ImmutableList.copyOf(validParameters);
  }

  public List<String> getValidParameters() {
    return validParameters;
  }

  public String getModifiedParametersHeaderPrefix() {
    return MODIFIED_PARAMETERS_HEADER_PREFIX;
  }
}
