package com.coremedia.blueprint.livecontext.ecommerce.filter;

import com.coremedia.springframework.web.ComponentWebApplicationInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.EnumSet;

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
  protected void configure(ServletContext servletContext) {
    FilterRegistration filterRegistration = servletContext.getFilterRegistration(COMMERCE_CONNECTION_FILTER);
    if(null == filterRegistration) {
      filterRegistration = servletContext.addFilter(COMMERCE_CONNECTION_FILTER, new DelegatingFilterProxy(COMMERCE_CONNECTION_FILTER));
      filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
    }
  }
}