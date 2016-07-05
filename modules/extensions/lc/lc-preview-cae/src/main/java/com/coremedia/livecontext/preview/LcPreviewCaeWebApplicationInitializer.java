package com.coremedia.livecontext.preview;

import com.coremedia.springframework.web.ComponentWebApplicationInitializer;
import org.springframework.core.annotation.Order;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.EnumSet;

@Order(20_100)
public class LcPreviewCaeWebApplicationInitializer extends ComponentWebApplicationInitializer {

  private static final String LIVECONTEXT = "livecontext";
  private static final String PREVIEW_TOKEN_APPENDER = "previewTokenAppender";
  private static final String WC_COOKIE_FILTER = "wcCookieFilter";

  @Override
  protected String getComponentName() {
    return LIVECONTEXT;
  }

  @Override
  protected void configure(ServletContext servletContext) {
    FilterRegistration filterRegistration = servletContext.getFilterRegistration(PREVIEW_TOKEN_APPENDER);
    if(null == filterRegistration) {
      filterRegistration = servletContext.addFilter(PREVIEW_TOKEN_APPENDER, new PreviewTokenMarkerFilter());
      filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
    }

    filterRegistration = servletContext.getFilterRegistration(WC_COOKIE_FILTER);
    if(null == filterRegistration) {
      filterRegistration = servletContext.addFilter(WC_COOKIE_FILTER, new WcCookieFilter());
      filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
    }
  }
}
