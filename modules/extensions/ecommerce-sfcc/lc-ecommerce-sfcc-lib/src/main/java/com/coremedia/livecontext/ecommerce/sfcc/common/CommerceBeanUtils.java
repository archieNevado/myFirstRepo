package com.coremedia.livecontext.ecommerce.sfcc.common;

import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.sfcc.beans.AbstractSfccCommerceBean;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

public class CommerceBeanUtils {

  private static final Logger LOG = LoggerFactory.getLogger(CommerceBeanUtils.class);

  private CommerceBeanUtils() {
  }

  /**
   * Creates a {@link CommerceBean} for the given delegate and target class.
   *
   * @param delegate delegate {@link AbstractOCDocument}
   * @param beanType type of the commerce bean
   * @param aClass   target class
   * @param <T>      type of the target {@link CommerceBean} class
   * @return {@link CommerceBean}
   */
  @Nullable
  public static <T extends CommerceBean> T createBeanFor(@Nonnull CommerceBeanFactory commerceBeanFactory,
                                                         @Nonnull AbstractOCDocument delegate,
                                                         @Nonnull StoreContext storeContext,
                                                         @Nonnull CommerceBeanType beanType,
                                                         @Nonnull Class<T> aClass) {
    return doCreateBeanFor(commerceBeanFactory, delegate, storeContext, beanType, aClass, false);
  }

  /**
   * Creates a list of {@link CommerceBean}s for the given delegates with the given target class.
   * This method marks the created beans as lightweight. "Lightweight" means that not all properties have been loaded yet.
   *
   * @param delegates list of delegates {@link AbstractOCDocument}
   * @param beanType  type of the commerce bean
   * @param aClass    target class
   * @param <T>       type of the target {@link CommerceBean} class
   * @return
   */
  public static <T extends CommerceBean> List<T> createLightweightBeansFor(@Nonnull CommerceBeanFactory commerceBeanFactory,
                                                                    @Nonnull List<? extends AbstractOCDocument> delegates,
                                                                    @Nonnull StoreContext storeContext,
                                                                    @Nonnull CommerceBeanType beanType,
                                                                    @Nonnull Class<T> aClass) {
    return doCreateLightWeightBeans(commerceBeanFactory, delegates, aClass, storeContext, beanType);
  }

  private static <T extends CommerceBean> T doCreateBeanFor(@Nonnull CommerceBeanFactory commerceBeanFactory,
                                                            @Nonnull AbstractOCDocument delegate,
                                                            @Nonnull StoreContext storeContext,
                                                            @Nonnull CommerceBeanType beanType,
                                                            @Nonnull Class<T> aClass,
                                                            boolean isLightWeight) {
    CommerceId commerceId = SfccCommerceIdProvider.commerceId(beanType).withExternalId(delegate.getId()).build();
    AbstractSfccCommerceBean bean = (AbstractSfccCommerceBean) commerceBeanFactory.createBeanFor(commerceId, storeContext);
    bean.setDelegate(delegate);
    bean.setLightweight(isLightWeight);
    LOG.debug("Created commerce bean for '{}' (lightweight={})", commerceId, isLightWeight);

    return aClass.cast(bean);
  }

  private static <T extends CommerceBean> List<T> doCreateLightWeightBeans(@Nonnull CommerceBeanFactory commerceBeanFactory,
                                                                           @Nonnull List<? extends AbstractOCDocument> delegates,
                                                                           @Nonnull Class<T> aClass,
                                                                           @Nonnull StoreContext storeContext,
                                                                           @Nonnull CommerceBeanType beanType) {
    if (delegates.isEmpty()) {
      return emptyList();
    }

    List<T> beans = delegates.stream()
            .map(delegate -> doCreateBeanFor(commerceBeanFactory, delegate, storeContext, beanType, aClass, true))
            .filter(Objects::nonNull)
            .collect(toList());

    return unmodifiableList(beans);
  }

}
