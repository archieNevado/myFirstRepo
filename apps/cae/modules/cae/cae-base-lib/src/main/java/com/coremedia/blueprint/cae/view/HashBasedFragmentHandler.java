package com.coremedia.blueprint.cae.view;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * A wrapper class for bean includes
 *
 * @cm.template.api
 */
@DefaultAnnotation(NonNull.class)
public class HashBasedFragmentHandler extends DynamicInclude {

  public static final String MODIFIED_PARAMETERS_HEADER_PREFIX = "CM_MODIFIED_PARAMETERS_";

  private final List<String> validParameters;

  public HashBasedFragmentHandler(Object delegate, @Nullable String view, Collection<String> validParameters) {
    super(delegate, view);
    this.validParameters = List.copyOf(validParameters);
  }

  /**
   * @cm.template.api
   */
  public List<String> getValidParameters() {
    return validParameters;
  }

  /**
   * @cm.template.api
   */
  public String getModifiedParametersHeaderPrefix() {
    return MODIFIED_PARAMETERS_HEADER_PREFIX;
  }
}
