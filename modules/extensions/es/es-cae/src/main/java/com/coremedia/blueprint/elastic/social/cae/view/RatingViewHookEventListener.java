package com.coremedia.blueprint.elastic.social.cae.view;

import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.blueprint.elastic.social.cae.controller.RatingResult;
import com.coremedia.objectserver.view.RenderNode;
import com.coremedia.objectserver.view.events.ViewHookEvent;
import com.coremedia.objectserver.view.events.ViewHookEventListener;

import javax.inject.Named;

/**
 * A {@link com.coremedia.objectserver.view.events.ViewHookEventListener} that
 * is responsible for adding the comments widget to rendered views.
 */
@Named
public class RatingViewHookEventListener implements ViewHookEventListener<CMTeasable> {

  @Override
  public RenderNode onViewHook(ViewHookEvent<CMTeasable> event) {
    // not yet implemented
    /*
    if(VIEW_HOOK_END.equals(event.getId())) {
      return new RenderNode(getRating(event.getBean()), null);
    }*/

    return null;
  }

  //====================================================================================================================

  private RatingResult getRating(Object target) {
    return new RatingResult(target);
  }

  @Override
  public int getOrder() {
    return DEFAULT_ORDER;
  }
}
