package com.coremedia.blueprint.studio.uitest.wrappers.moderation.model;

import com.coremedia.elastic.core.api.models.Model;
import com.coremedia.uitesting.ui.data.RemoteBean;
import net.joala.condition.Condition;
import net.joala.condition.ConditionFactory;
import net.joala.expression.AbstractExpression;

import javax.inject.Inject;

/**
 * A Java representation of a cap-rest-client Moderated Items such as Comments and Users.
 */
public abstract class ModeratedItem<T extends Model> extends RemoteBean<T> {
  @SuppressWarnings("SpringJavaAutowiringInspection") // NOSONAR
  @Inject
  private ConditionFactory conditionFactory;

  /**
   * Retrieve the item representing the result of the embedded expression.
   *
   * @return the moderated item
   * @see #bean()
   */
  @Override
  public T getBean() {
    return getBeanById(targetId().await());
  }

  protected abstract T getBeanById(final String id);

  public Condition<String> targetId() {
    return stringCondition("self.getBean().getTargetId()");
  }

  /**
   * <p>
   * Get condition to retrieve the item. Might be especially used if you wait for
   * a remote bean representation to be available.
   * </p>
   *
   * @return condition
   */
  @Override
  @SuppressWarnings("AnonymousInnerClass")
  public Condition<T> bean() {
    return conditionFactory.condition(new AbstractExpression<T>() {
      @Override
      public T get() {
        return getBean();
      }
    });
  }

}
