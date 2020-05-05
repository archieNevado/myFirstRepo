package com.coremedia.blueprint.studio.uitest.wrappers.moderation.labs;

import com.coremedia.uitesting.ui.data.RemoteBean;
import com.coremedia.uitesting.webdriver.JsArgumentPreProcessor;
import com.coremedia.uitesting.webdriver.JsExpression;
import net.joala.condition.BooleanCondition;

/**
 * <p>
 * Represents components which can contain a remote-beans.
 * </p>
 *
 * @param <B> the original type which is wrapped in a remote bean
 * @param <T> the type of the remote bean
 */
public interface RemoteBeanContainer<B, T extends RemoteBean<? extends B>> {
  /**
   * <p>
   * Condition to verify that a given bean representation is contained
   * in this container.
   * </p>
   * <p>
   * <strong>Precondition:</strong> Requires a {@link JsArgumentPreProcessor} capable to create
   * a {@link JsExpression} from the given type.
   * </p>
   *
   * @param bean bean to search for
   * @return condition
   */
  BooleanCondition contains(B bean);

  /**
   * <p>
   * Condition to verify that a given remote bean representation is contained
   * in this container.
   * </p>
   *
   * @param remoteBean remote bean to search for
   * @return condition
   */
  BooleanCondition contains(T remoteBean);
}
