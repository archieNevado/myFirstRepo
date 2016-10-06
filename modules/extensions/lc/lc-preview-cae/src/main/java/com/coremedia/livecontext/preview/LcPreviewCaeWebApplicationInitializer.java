package com.coremedia.livecontext.preview;

import com.coremedia.springframework.web.ComponentWebApplicationInitializer;
import com.coremedia.springframework.web.RegistrationBeanBuilder;
import com.google.common.collect.ImmutableList;
import org.springframework.boot.context.embedded.RegistrationBean;
import org.springframework.core.annotation.Order;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

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
  protected void configure(@Nonnull ServletContext servletContext) {
    // nothing to configure
  }

  @Nonnull
  @Override
  protected Iterable<RegistrationBean> createRegistrationBeans() {
    return ImmutableList.of(
            RegistrationBeanBuilder.forFilter(new PreviewTokenMarkerFilter()).name(PREVIEW_TOKEN_APPENDER).build(),
            RegistrationBeanBuilder.forFilter(new WcCookieFilter()).name(WC_COOKIE_FILTER).build()
    );
  }
}
