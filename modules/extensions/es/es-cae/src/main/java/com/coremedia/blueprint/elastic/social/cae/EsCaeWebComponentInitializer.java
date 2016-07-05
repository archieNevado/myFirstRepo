package com.coremedia.blueprint.elastic.social.cae;

import com.coremedia.springframework.web.ComponentWebApplicationInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.EnumSet;

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

  private static final Logger LOGGER = LoggerFactory.getLogger(EsCaeWebComponentInitializer.class);

  private static final String ELASTIC_SOCIAL = "elastic-social";
  private static final String SERVLET_DYNAMIC = "/servlet/dynamic/*";
  private static final String SERVLET_RESOURCE_ELASTIC = "/servlet/resource/elastic/*";

  @Override
  protected String getComponentName() {
    return ELASTIC_SOCIAL;
  }

  @Override
  protected void configure(ServletContext servletContext) {
    // These filters MUST run before all other filters, otherwise we violate the contract with Spring Security.
    FilterRegistration springSecurityFilterChain = servletContext.getFilterRegistration(SPRING_SECURITY_FILTER_CHAIN);
    if(null == springSecurityFilterChain) {
      LOGGER.info("setting up spring security filter chain");
      springSecurityFilterChain = servletContext.addFilter(SPRING_SECURITY_FILTER_CHAIN, new DelegatingFilterProxy());
    } else {
      LOGGER.info("spring security filter chain already set up");
    }
    springSecurityFilterChain.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true,  "/*");

    FilterRegistration.Dynamic guidFilter = servletContext.addFilter(GUID_FILTER, new DelegatingFilterProxy());
    guidFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, SERVLET_DYNAMIC);

    FilterRegistration siteFilter = servletContext.getFilterRegistration(SITE_FILTER);
    if(null == siteFilter) {
      siteFilter = servletContext.addFilter(SITE_FILTER, new DelegatingFilterProxy());
    }
    siteFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/servlet/*");

    FilterRegistration.Dynamic sessionSiteFilter = servletContext.addFilter(SESSION_SITE_FILTER, new DelegatingFilterProxy());
    sessionSiteFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, SERVLET_DYNAMIC);

    FilterRegistration tenantFilter = servletContext.getFilterRegistration(TENANT_FILTER);
    if(null == tenantFilter) {
      LOGGER.info("setting up tenant filter");
      tenantFilter = servletContext.addFilter(TENANT_FILTER, new DelegatingFilterProxy());
    } else {
      LOGGER.info("tenant filter already set up");
    }
    tenantFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, SERVLET_DYNAMIC);

    FilterRegistration.Dynamic userFilter = servletContext.addFilter(USER_FILTER, new DelegatingFilterProxy());
    userFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, SERVLET_DYNAMIC, SERVLET_RESOURCE_ELASTIC);

    /**
     * Publishing HTTP session events to spring's root web application context
     */
    servletContext.addListener(new HttpSessionEventPublisher());
  }
}
