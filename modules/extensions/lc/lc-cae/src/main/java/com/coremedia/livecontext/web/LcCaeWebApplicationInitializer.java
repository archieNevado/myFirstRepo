package com.coremedia.livecontext.web;

import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.springframework.web.ComponentWebApplicationInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.EnumSet;

@Order(20_000)
public class LcCaeWebApplicationInitializer extends ComponentWebApplicationInitializer {

  private static final String LIVECONTEXT = "livecontext";
  private static final String FRAGMENT_CONTEXT_PROVIDER = "fragmentContextProvider";
  private static final String COOKIE_LEVELER = "cookieLeveler";

  @Override
  protected String getComponentName() {
    return LIVECONTEXT;
  }

  @Override
  protected void configure(ServletContext servletContext) {
    FilterRegistration filterRegistration = servletContext.getFilterRegistration(FRAGMENT_CONTEXT_PROVIDER);
    if(null == filterRegistration) {
      filterRegistration = servletContext.addFilter(FRAGMENT_CONTEXT_PROVIDER, new FragmentContextProvider());
      filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
    }

    filterRegistration = servletContext.getFilterRegistration(COOKIE_LEVELER);
    if(null == filterRegistration) {
      filterRegistration = servletContext.addFilter(COOKIE_LEVELER, new DelegatingFilterProxy(COOKIE_LEVELER));
      filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
    }
  }
}
