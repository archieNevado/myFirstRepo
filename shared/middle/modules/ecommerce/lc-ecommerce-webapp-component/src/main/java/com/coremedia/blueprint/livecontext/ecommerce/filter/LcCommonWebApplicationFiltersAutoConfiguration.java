package com.coremedia.blueprint.livecontext.ecommerce.filter;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.springframework.boot.web.servlet.RegistrationBeanBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Set up filter proxy for commerce connection filter
 */
@Configuration(proxyBeanMethods = false)
public class LcCommonWebApplicationFiltersAutoConfiguration {

  private static final String COMMERCE_CONNECTION_FILTER = "commerceConnectionFilter";

  @Bean
  public FilterRegistrationBean createRegistrationBeans(CommerceConnectionSupplier commerceConnectionSupplier) {
    return RegistrationBeanBuilder
            .forFilter(new CommerceConnectionFilter(commerceConnectionSupplier))
            .name(COMMERCE_CONNECTION_FILTER)
            .order(2000)
            .build();
  }

}
