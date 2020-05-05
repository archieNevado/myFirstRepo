package com.coremedia.blueprint.studio.uitest.wrappers.moderation.model;

import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.uitesting.webdriver.JsArgumentPreProcessorBase;
import com.coremedia.uitesting.webdriver.JsExpression;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * This Argument Pre-Processor wraps {@link Comment} objects to remote bean expressions.
 * This way you can add Comments as normal argument to any JsExpression.
 */
@Named
@Singleton
public class CommentJsArgumentPreProcessor extends JsArgumentPreProcessorBase<Comment> {
  @Inject
  private ModeratedItemFactory moderatedItemFactory;

  @Override
  public JsExpression process(final Comment obj) {
    return moderatedItemFactory.get(obj).getExpression();
  }
}
