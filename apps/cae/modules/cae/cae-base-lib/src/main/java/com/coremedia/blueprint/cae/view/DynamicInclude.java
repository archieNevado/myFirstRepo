package com.coremedia.blueprint.cae.view;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * A wrapper class for dynamic bean includes
 *
 * @cm.template.api
 */
@DefaultAnnotation(NonNull.class)
public class DynamicInclude extends com.coremedia.objectserver.view.dynamic.DynamicInclude {

  public DynamicInclude(Object delegate, @Nullable String view) {
    super(delegate, view);
  }

}
