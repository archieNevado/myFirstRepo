package com.coremedia.blueprint.livecontext.ecommerce.filter;

import com.coremedia.springframework.web.ComponentWebApplicationInitializer;
import com.coremedia.springframework.web.RegistrationBeanBuilder;
import org.springframework.boot.web.servlet.RegistrationBean;
import org.springframework.core.annotation.Order;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import java.util.Collections;

/**
 * Set up filter proxy for commerce connection filter
 */
@Order(2_000)
public class LcCommonWebApplicationInitializer extends ComponentWebApplicationInitializer {

  private static final String COMMERCE_CONNECTION_FILTER = "commerceConnectionFilter";
  private static final String LC_ECOMMERCE_WEBAPP = "lc-ecommerce-webapp";

  @Override
  protected String getComponentName() {
    return LC_ECOMMERCE_WEBAPP;
  }

  @Override
  protected void configure(@Nonnull ServletContext servletContext) {
    // nothing to configure
  }

  @Nonnull
  @Override
  protected Iterable<RegistrationBean> createRegistrationBeans() {
    RegistrationBean bean = RegistrationBeanBuilder
            .forFilterProxy(COMMERCE_CONNECTION_FILTER)
            .name(COMMERCE_CONNECTION_FILTER)
            .build();

    return Collections.singleton(bean);
  }
}
