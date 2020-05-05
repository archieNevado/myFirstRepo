package com.coremedia.blueprint.studio.uitest.wrappers.moderation.labs;

import com.coremedia.uitesting.ui.data.RemoteBean;
import com.coremedia.uitesting.webdriver.JsArgumentPreProcessor;
import com.coremedia.uitesting.webdriver.JsExpression;
import net.joala.condition.BooleanCondition;
import net.joala.condition.Condition;

/**
 * <p>
 * Specialized {@link RemoteBeanContainer} which can contain multiple remote beans in a given order.
 * </p>
 *
 * @param <B> the original type which is wrapped in a remote bean
 * @param <T> the type of the remote bean
 */
public interface RemoteBeanListContainer<B, T extends RemoteBean<? extends B>> extends RemoteBeanContainer<B, T> {
  /**
   * <p>
   * Find the position of the given bean in the container.
   * </p>
   * <p>
   * <strong>Precondition:</strong> Requires a {@link JsArgumentPreProcessor} capable to create
   * a {@link JsExpression} from the given type.
   * </p>
   *
   * @param bean bean to search for
   * @return condition for bean position (starting from 0); -1 if content is not found
   */
  Condition<Long> position(B bean);

  /**
   * <p>
   * Find the position of the given remote bean in the container.
   * </p>
   *
   * @param remoteBean bean to search for
   * @return condition for remote bean position (starting from 0); -1 if content is not found
   */
  Condition<Long> position(T remoteBean);

  /**
   * <p>
   * Condition to verify that the given bean appears at the given position.
   * </p>
   * <p>
   * <strong>Precondition:</strong> Requires a {@link JsArgumentPreProcessor} capable to create
   * a {@link JsExpression} from the given type.
   * </p>
   *
   * @param position position to check; starting from 0
   * @param bean     bean to search for
   * @return condition
   */
  BooleanCondition isAt(long position, B bean);

  /**
   * <p>
   * Condition to verify that the given remote bean appears at the given position.
   * </p>
   *
   * @param position   position to check; starting from 0
   * @param remoteBean bean to search for
   * @return condition
   */
  BooleanCondition isAt(long position, T remoteBean);
}
