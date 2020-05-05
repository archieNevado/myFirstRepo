package com.coremedia.blueprint.uitesting.elastic.service.util.review;

import org.springframework.beans.factory.BeanFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * <p>
 * Provides instances of review builders.
 * </p>
 *
 * @since 2014-06-30
 */
@Named
@Singleton
public final class DefaultReviewBuilderProvider implements Provider<ReviewBuilder> {
  @Inject
  private BeanFactory beanFactory;

  @Override
  public ReviewBuilder get() {
    return beanFactory.getBean(DefaultReviewBuilder.class);
  }
}
