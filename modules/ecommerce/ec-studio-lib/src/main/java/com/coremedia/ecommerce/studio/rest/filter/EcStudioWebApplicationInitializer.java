package com.coremedia.ecommerce.studio.rest.filter;

import com.coremedia.springframework.web.ComponentWebApplicationInitializer;
import com.coremedia.springframework.web.RegistrationBeanBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.RegistrationBean;
import org.springframework.core.annotation.Order;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

import static java.util.Collections.singletonList;

/**
 * Site filter configuration for Studio
 */
@Order(1_000)
public class EcStudioWebApplicationInitializer extends ComponentWebApplicationInitializer {

  private static final String SITE_FILTER = "siteFilter";
  private static final String EC_STUDIO_LIB = "ec-studio-lib";

  @Override
  protected String getComponentName() {
    return EC_STUDIO_LIB;
  }

  @Override
  protected void configure(@Nonnull ServletContext servletContext) {
    // nothing to do
  }

  @Nonnull
  @Override
  protected Iterable<RegistrationBean> createRegistrationBeans() {
    FilterRegistrationBean bean = RegistrationBeanBuilder
            .forFilterProxy(SITE_FILTER)
            .name(SITE_FILTER)
            .urlPatterns("/api/livecontext/*")
            .build();

    return singletonList(bean);
  }
}
