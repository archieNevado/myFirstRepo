package com.coremedia.livecontext.marketingspot;

import com.coremedia.blueprint.cae.view.DynamicIncludePredicate;
import com.coremedia.livecontext.contentbeans.CMMarketingSpot;
import com.coremedia.objectserver.view.RenderNode;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.regex.Pattern;

import static com.coremedia.blueprint.cae.view.DynamicIncludeHelper.isAlreadyIncludedDynamically;

/**
 * Predicate to determine if a node to render is dynamic include of {@link com.coremedia.livecontext.contentbeans.CMMarketingSpot}.
 */
public class CMMarketingSpotDynamicIncludePredicate implements DynamicIncludePredicate {

  private static Pattern VIEW_EXCLUDE_PATTERN = Pattern.compile("^fragmentPreview(\\[[^\\]]*\\]){0,1}$");

  @Override
  public boolean apply(@Nullable RenderNode input) {
    if (input == null) {
      return false;
    } else if (input.getBean() instanceof CMMarketingSpot && !isAlreadyIncludedDynamically()) {
      if (input.getView() == null || !VIEW_EXCLUDE_PATTERN.matcher(input.getView()).matches()) {
        return true;
      }
    }
    return false;
  }
}
