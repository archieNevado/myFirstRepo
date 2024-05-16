package com.coremedia.blueprint.elastic.social.cae.view;

import com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState;
import com.coremedia.objectserver.view.RenderNode;
import com.coremedia.objectserver.view.dynamic.DynamicIncludePredicate;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;

/**
 * Return true if {@link AuthenticationState} beans are rendered with one of the following views:
 * "asButton", "asHeader", "asLink",
 */
@DefaultAnnotation(NonNull.class)
public class ESDynamicIncludePredicate implements DynamicIncludePredicate {

  private static final List<String> VIEW_NAMES = List.of("asButton", "asHeader", "asLink");

  @Override
  public boolean test(RenderNode input) {
    String view = input.getView();
    return input.getBean() instanceof AuthenticationState && view != null && VIEW_NAMES.contains(view);
  }
}
