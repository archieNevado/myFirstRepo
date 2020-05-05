package com.coremedia.blueprint.uitesting.elastic.service.util.comment;

import org.springframework.beans.factory.BeanFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * <p>
 * Provides instances of comment builders.
 * </p>
 *
 * @since 2013-02-14
 */
@Named
@Singleton
public final class DefaultCommentBuilderProvider implements Provider<CommentBuilder> {
  @Inject
  private BeanFactory beanFactory;

  @Override
  public CommentBuilder get() {
    return beanFactory.getBean(DefaultCommentBuilder.class);
  }
}
