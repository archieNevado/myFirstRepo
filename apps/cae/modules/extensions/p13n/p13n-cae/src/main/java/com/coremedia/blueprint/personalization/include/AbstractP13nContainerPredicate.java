package com.coremedia.blueprint.personalization.include;

import com.coremedia.blueprint.cae.view.DynamicIncludeHelper;
import com.coremedia.blueprint.cae.view.DynamicIncludePredicate;
import com.coremedia.objectserver.view.RenderNode;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.regex.Pattern;

public abstract class AbstractP13nContainerPredicate implements DynamicIncludePredicate {

  private static Pattern VIEW_EXCLUDE_PATTERN = Pattern.compile("^fragmentPreview(\\[[^]]*])?$");
  private static final String VIEW_NAME_AS_PREVIEW = "asPreview";
  private static final String MULTI_VIEW_PREVIEW = "multiViewPreview";

  @Override
  public boolean apply(@Nullable RenderNode input) {
    if (input == null) {
      return false;
    }
    if (DynamicIncludeHelper.isAlreadyIncludedDynamically()) {
      return false;
    }
    if (isViewMatching(input.getView())) {
      return isBeanMatching(input.getBean());
    }
    return false;
  }

  protected boolean isViewMatching(String view) {
    return view == null || !(VIEW_EXCLUDE_PATTERN.matcher(view).matches() || view.equals(VIEW_NAME_AS_PREVIEW) || view.equals(MULTI_VIEW_PREVIEW));
  }

  protected abstract boolean isBeanMatching(Object bean);
}
