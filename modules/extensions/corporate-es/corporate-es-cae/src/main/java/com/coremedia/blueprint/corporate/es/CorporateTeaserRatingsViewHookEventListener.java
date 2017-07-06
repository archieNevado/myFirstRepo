package com.coremedia.blueprint.corporate.es;

import com.coremedia.blueprint.ecommerce.contentbeans.CMProduct;
import com.coremedia.blueprint.elastic.social.cae.controller.ReviewsResult;
import com.coremedia.objectserver.view.RenderNode;
import com.coremedia.objectserver.view.events.ViewHookEvent;
import com.coremedia.objectserver.view.events.ViewHookEventListener;

import javax.inject.Named;

import static com.coremedia.blueprint.base.cae.web.taglib.ViewHookEventNames.VIEW_HOOK_SEARCH;
import static com.coremedia.blueprint.base.cae.web.taglib.ViewHookEventNames.VIEW_HOOK_TEASER;


/**
 * A {@link ViewHookEventListener} that
 * is responsible for adding the average rating widget to rendered views.
 */
@Named
public class CorporateTeaserRatingsViewHookEventListener implements ViewHookEventListener<CMProduct> {

  @Override
  public RenderNode onViewHook(ViewHookEvent<CMProduct> event) {
    if (VIEW_HOOK_TEASER.equals(event.getId()) || VIEW_HOOK_SEARCH.equals(event.getId())) {
      return new RenderNode(getReviewsResult(event.getBean()), "asAverageRating");
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
