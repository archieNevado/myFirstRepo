package com.coremedia.blueprint.studio.uitest.wrappers.moderation.model;

import com.coremedia.elastic.core.api.users.User;
import com.coremedia.uitesting.webdriver.JsArgumentPreProcessorBase;
import com.coremedia.uitesting.webdriver.JsExpression;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * This Argument Pre-Processor wraps {@link User} objects to remote bean expressions.
 * This way you can add Users as normal argument to any JsExpression.
 */
@Named
@Singleton
public class UserJsArgumentPreProcessor extends JsArgumentPreProcessorBase<User> {
  @Inject
  private ModeratedItemFactory moderatedItemFactory;

  @Override
  public JsExpression process(final User obj) {
    return moderatedItemFactory.get(obj).getExpression();
  }
}
