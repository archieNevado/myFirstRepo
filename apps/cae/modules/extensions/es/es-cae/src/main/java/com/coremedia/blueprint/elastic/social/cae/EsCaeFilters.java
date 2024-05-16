package com.coremedia.blueprint.elastic.social.cae;

import com.coremedia.springframework.boot.web.servlet.RegistrationBeanBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Configuration(proxyBeanMethods = false)
public class EsCaeFilters {
  private static final String SERVLET = "/servlet/*";
  private static final String SERVLET_DYNAMIC = "/servlet/dynamic/*";
  private static final String SERVLET_RESOURCE_ELASTIC = "/servlet/resource/elastic/*";

  // site filter is registered in com.coremedia.blueprint.component.cae.CaeBaseComponentConfiguration with order 100
  private static final int ORDER_SITE_FILTER = 100;

  private static final int ORDER_SPRING_SECURITY_FILTER_CHAIN = HIGHEST_PRECEDENCE + 1_147_483_648; // == -1_000_000_000

  @Bean
  public FilterRegistrationBean springSecurityFilterChainRegistration(Filter springSecurityFilterChain) {
    return RegistrationBeanBuilder
            .forFilter(springSecurityFilterChain)
            .order(ORDER_SPRING_SECURITY_FILTER_CHAIN)
            .build();
  }

  @Bean
  public FilterRegistrationBean sessionSiteFilterRegistration(Filter sessionSiteFilter) {
    return RegistrationBeanBuilder
            .forFilter(sessionSiteFilter)
            .urlPatterns(SERVLET_DYNAMIC)
            .order(ORDER_SITE_FILTER + 10)
            .build();
  }

  @Bean
  public FilterRegistrationBean tenantFilterRegistration(Filter tenantFilter) {
    return RegistrationBeanBuilder
            .forFilter(tenantFilter)
            .urlPatterns(SERVLET)
            .order(ORDER_SITE_FILTER + 20)
            .build();
  }

  @Bean
  public FilterRegistrationBean userFilterRegistration(Filter userFilter) {
    return RegistrationBeanBuilder
            .forFilter(userFilter)
            .urlPatterns(SERVLET_DYNAMIC, SERVLET_RESOURCE_ELASTIC)
            .order(ORDER_SITE_FILTER + 30)
            .build();
  }

}
