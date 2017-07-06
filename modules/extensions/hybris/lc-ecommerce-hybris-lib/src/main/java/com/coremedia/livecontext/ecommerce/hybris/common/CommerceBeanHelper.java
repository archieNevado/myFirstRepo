package com.coremedia.livecontext.ecommerce.hybris.common;

import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.beans.AbstractHybrisCommerceBean;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.AbstractHybrisDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommerceBeanHelper {

  private static final Logger LOG = LoggerFactory.getLogger(CommerceBeanHelper.class);

  private CommerceBeanFactory commerceBeanFactory;

  /**
   * Creates a {@link CommerceBean} for the given delegate and target class.
   *
   * @param delegate delegate {@link AbstractHybrisDocument}
   * @param aClass   target class
   * @param <T>      type of the target {@link CommerceBean} class
   * @return {@link CommerceBean}
   */
  @Nullable
  public <T extends CommerceBean> T createBeanFor(@Nullable AbstractHybrisDocument delegate, @Nonnull Class<T> aClass) {
    if (delegate == null) {
      return null;
    }

    String id = CommerceIdHelper.convertToInternalId(delegate.getCode(), aClass);

    AbstractHybrisCommerceBean bean = getAbstractHybrisCommerceBean(id);
    bean.setDelegate(delegate);
    LOG.debug("Created commerce bean for '{}'", id);

    return aClass.cast(bean);
  }

  /**
   * Creates a {@link CommerceBean} for the given externalId and target class.
   *
   * @param externalId external Id
   * @param aClass     target class
   * @param <T>        type of the target {@link CommerceBean} class
   * @return {@link CommerceBean}
   */
  @Nullable
  public <T extends CommerceBean> T createBeanFor(@Nullable String externalId, @Nonnull Class<T> aClass) {
    if (externalId == null) {
      return null;
    }

    String id = CommerceIdHelper.convertToInternalId(externalId, aClass);

    AbstractHybrisCommerceBean bean = getAbstractHybrisCommerceBean(id);
    LOG.debug("Created commerce bean for '{}'", id);

    return aClass.cast(bean);
  }

  @Nullable
  private AbstractHybrisCommerceBean getAbstractHybrisCommerceBean(@Nonnull String id) {
    StoreContext context = StoreContextHelper.getCurrentContext();
    return (AbstractHybrisCommerceBean) commerceBeanFactory.createBeanFor(id, context);
  }

  /**
   * Creates a list of {@link CommerceBean}s for the given delegates with the given target class.
   *
   * @param delegates list of delegates {@link AbstractHybrisDocument}
   * @param aClass    target class
   * @param <T>       type of the target {@link CommerceBean} class
   * @return {@link CommerceBean}
   */
  @Nonnull
  public <T extends CommerceBean> List<T> createBeansFor(@Nullable List<? extends AbstractHybrisDocument> delegates,
                                                         @Nonnull Class<T> aClass) {
    if (delegates == null || delegates.isEmpty()) {
      return Collections.emptyList();
    }

    List<T> result = new ArrayList<>(delegates.size());
    for (AbstractHybrisDocument delegate : delegates) {
      T bean = createBeanFor(delegate, aClass);
      if (bean != null) {
        result.add(bean);
      }
    }

    return Collections.unmodifiableList(result);
  }

  public CommerceBeanFactory getCommerceBeanFactory() {
    return commerceBeanFactory;
  }

  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }
}
