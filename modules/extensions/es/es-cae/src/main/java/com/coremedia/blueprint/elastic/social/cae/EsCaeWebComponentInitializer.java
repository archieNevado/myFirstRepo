package com.coremedia.blueprint.elastic.social.cae;

import com.coremedia.springframework.web.ComponentWebApplicationInitializer;
import com.coremedia.springframework.web.RegistrationBeanBuilder;
import com.google.common.collect.ImmutableList;
import org.springframework.boot.web.servlet.RegistrationBean;
import org.springframework.core.annotation.Order;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

/**
 * In addition to elastic social filters to gather tenant and site information,
 * this component application initializer configures spring security which must be
 * setup early.
 */
@Order(50)
public final class EsCaeWebComponentInitializer extends ComponentWebApplicationInitializer {

  public static final String SITE_FILTER = "siteFilter";
  public static final String SPRING_SECURITY_FILTER_CHAIN = "springSecurityFilterChain";
  public static final String GUID_FILTER = "guidFilter";
  public static final String SESSION_SITE_FILTER = "sessionSiteFilter";
  public static final String TENANT_FILTER = "tenantFilter";
  public static final String USER_FILTER = "userFilter";

  private static final String ELASTIC_SOCIAL = "elastic-social";
  private static final String SERVLET = "/servlet/*";
  private static final String SERVLET_DYNAMIC = "/servlet/dynamic/*";
  private static final String SERVLET_RESOURCE_ELASTIC = "/servlet/resource/elastic/*";

  @Override
  protected String getComponentName() {
    return ELASTIC_SOCIAL;
  }

  @Override
  protected void configure(@Nonnull ServletContext servletContext) {
    // nothing to configure
  }

  @Nonnull
  @Override
  protected Iterable<RegistrationBean> createRegistrationBeans() {
    ImmutableList.Builder<RegistrationBean> builder = ImmutableList.builder();

    builder.add(RegistrationBeanBuilder
            .forFilterProxy(SPRING_SECURITY_FILTER_CHAIN)
            .name(SPRING_SECURITY_FILTER_CHAIN)
            .build());

    builder.add(RegistrationBeanBuilder
            .forFilterProxy(GUID_FILTER)
            .name(GUID_FILTER)
            .urlPatterns(SERVLET_DYNAMIC)
            .build());

    builder.add(RegistrationBeanBuilder
            .forFilterProxy(SITE_FILTER)
            .name(SITE_FILTER)
            .urlPatterns(SERVLET)
            .build());

    builder.add(RegistrationBeanBuilder
            .forFilterProxy(SESSION_SITE_FILTER)
            .name(SESSION_SITE_FILTER)
            .urlPatterns(SERVLET_DYNAMIC)
            .build());

    builder.add(RegistrationBeanBuilder
            .forFilterProxy(TENANT_FILTER)
            .name(TENANT_FILTER)
            .urlPatterns(SERVLET)
            .build());

    builder.add(RegistrationBeanBuilder
            .forFilterProxy(USER_FILTER)
            .name(USER_FILTER)
            .urlPatterns(SERVLET_DYNAMIC, SERVLET_RESOURCE_ELASTIC)
            .build());

    return builder.build();
  }
}
