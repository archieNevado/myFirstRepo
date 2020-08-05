package com.coremedia.blueprint.personalization.include;

import com.coremedia.objectserver.view.RenderNode;
import com.coremedia.objectserver.view.dynamic.DynamicIncludePredicate;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.regex.Pattern;

import static com.coremedia.blueprint.cae.view.DynamicIncludeHelper.isAlreadyIncludedDynamically;

import static com.coremedia.blueprint.common.datevalidation.ValidUntilConsumer.DISABLE_VALIDITY_RECORDING_ATTRIBUTE;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

@DefaultAnnotation(NonNull.class)
public abstract class AbstractP13nContainerPredicate implements DynamicIncludePredicate {

  //the length of the view type should be limited. Using '*' may cause ReDoS vulnerability
  private static final Pattern VIEW_EXCLUDE_PATTERN = Pattern.compile("^fragmentPreview(\\[[^]]{0,50}])?$");
  private static final String VIEW_NAME_AS_PREVIEW = "asPreview";
  private static final String MULTI_VIEW_PREVIEW = "multiViewPreview";

  @Override
  public boolean test(RenderNode input) {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes == null) {
      throw new IllegalStateException("Request attributes not available.");
    }
    try {
      requestAttributes.setAttribute(DISABLE_VALIDITY_RECORDING_ATTRIBUTE, true, SCOPE_REQUEST);
      if (isAlreadyIncludedDynamically()) {
        return false;
      }
      if (isViewMatching(input.getView())) {
        return isBeanMatching(input.getBean());
      }
      return false;
    } finally {
      requestAttributes.removeAttribute(DISABLE_VALIDITY_RECORDING_ATTRIBUTE, SCOPE_REQUEST);
    }
  }

  protected boolean isViewMatching(@Nullable String view) {
    return view == null || !(VIEW_EXCLUDE_PATTERN.matcher(view).matches() || view.equals(VIEW_NAME_AS_PREVIEW) || view.equals(MULTI_VIEW_PREVIEW));
  }

  protected abstract boolean isBeanMatching(Object bean);
}
