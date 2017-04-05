package com.coremedia.blueprint.corporate.es;

import com.coremedia.blueprint.ecommerce.contentbeans.CMProduct;
import com.coremedia.blueprint.elastic.social.cae.controller.ReviewsResult;
import com.coremedia.objectserver.view.RenderNode;
import com.coremedia.objectserver.view.events.ViewHookEvent;
import com.coremedia.objectserver.view.events.ViewHookEventListener;

import javax.inject.Named;

import static com.coremedia.blueprint.base.cae.web.taglib.ViewHookEventNames.VIEW_HOOK_END;


/**
 * A {@link com.coremedia.objectserver.view.events.ViewHookEventListener} that
 * is responsible for adding the review widget to rendered views.
 */
@Named
public class CorporateReviewsViewHookEventListener implements ViewHookEventListener<CMProduct> {

  @Override
  public RenderNode onViewHook(ViewHookEvent<CMProduct> event) {
    if (VIEW_HOOK_END.equals(event.getId())) {
      return new RenderNode(getReviewsResult(event.getBean()), null);
    }

    return null;
  }

  //====================================================================================================================

  private ReviewsResult getReviewsResult(Object target) {
    return new ReviewsResult(target);
  }

  @Override
  public int getOrder() {
    return DEFAULT_ORDER;
  }
}
