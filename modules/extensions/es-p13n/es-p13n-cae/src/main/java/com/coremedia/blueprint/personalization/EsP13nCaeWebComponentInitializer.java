package com.coremedia.blueprint.personalization;

import com.coremedia.blueprint.elastic.social.cae.EsCaeWebComponentInitializer;
import com.coremedia.springframework.web.ComponentWebApplicationInitializer;
import org.springframework.core.annotation.Order;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.EnumSet;

@Order(11_000)
public class EsP13nCaeWebComponentInitializer extends ComponentWebApplicationInitializer {

  private static final String ES_P13N_CAE = "es-p13n-cae";

  @Override
  protected String getComponentName() {
    return ES_P13N_CAE;
  }

  @Override
  protected void configure(ServletContext servletContext) {
    FilterRegistration filterRegistration = servletContext.getFilterRegistration(EsCaeWebComponentInitializer.USER_FILTER);
    if(null != filterRegistration) {
      filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/servlet/userdetails/*");
    }
  }
}
