package com.coremedia.livecontext.web;

import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.springframework.web.ComponentWebApplicationInitializer;
import com.coremedia.springframework.web.RegistrationBeanBuilder;
import com.google.common.collect.ImmutableList;
import org.springframework.boot.web.servlet.RegistrationBean;
import org.springframework.core.annotation.Order;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

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
  protected void configure(@Nonnull ServletContext servletContext) {
    // nothing to configure
  }

  @Nonnull
  @Override
  protected Iterable<RegistrationBean> createRegistrationBeans() {
    return ImmutableList.of(
            RegistrationBeanBuilder.forFilter(new FragmentContextProvider()).name(FRAGMENT_CONTEXT_PROVIDER).build(),
            RegistrationBeanBuilder.forFilterProxy(COOKIE_LEVELER).name(COOKIE_LEVELER).build()
    );
  }
}
