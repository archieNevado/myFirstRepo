package com.coremedia.blueprint.elastic.social.cae.view;

import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.blueprint.elastic.social.cae.controller.CommentsResult;
import com.coremedia.objectserver.view.RenderNode;
import com.coremedia.objectserver.view.events.ViewHookEvent;
import com.coremedia.objectserver.view.events.ViewHookEventListener;

import javax.inject.Named;

import static com.coremedia.blueprint.base.cae.web.taglib.ViewHookEventNames.VIEW_HOOK_END;

/**
 * A {@link com.coremedia.objectserver.view.events.ViewHookEventListener} that
 * is responsible for adding the comments widget to rendered views.
 */
@Named
public class CommentsViewHookEventListener implements ViewHookEventListener<CMTeasable> {

  @Override
  public RenderNode onViewHook(ViewHookEvent<CMTeasable> event) {
    if (VIEW_HOOK_END.equals(event.getId())) {
      return new RenderNode(getCommentsResult(event.getBean()), null);
    }

    return null;
  }

  //====================================================================================================================

  private CommentsResult getCommentsResult(Object target) {
    return new CommentsResult(target);
  }

  @Override
  public int getOrder() {
    return DEFAULT_ORDER;
  }
}
