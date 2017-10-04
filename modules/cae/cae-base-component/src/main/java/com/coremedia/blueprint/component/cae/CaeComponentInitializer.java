package com.coremedia.blueprint.component.cae;

import com.coremedia.blueprint.cae.filter.UnknownMimetypeCharacterEncodingFilter;
import com.coremedia.springframework.web.ComponentWebApplicationInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.RegistrationBean;
import org.springframework.core.annotation.Order;
import org.springframework.mobile.device.DeviceResolverRequestFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.annotation.Nonnull;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumSet;

@Order(1_000)
public final class CaeComponentInitializer extends ComponentWebApplicationInitializer {

  public static final String CHARACTER_ENCODING_FILTER = "characterEncodingFilter";
  public static final String DEVICE_RESOLVER_REQUEST_FILTER = "deviceResolverRequestFilter";
  public static final String SITE_FILTER = "siteFilter";

  private static final Logger LOG = LoggerFactory.getLogger(CaeComponentInitializer.class);

  private static final String CAE = "blueprint-cae";

  @Override
  protected String getComponentName() {
    return CAE;
  }

  @Override
  protected void configure(@Nonnull ServletContext servletContext) {
    FilterRegistration characterEncodingFilter = servletContext.getFilterRegistration(CHARACTER_ENCODING_FILTER);
    if(null == characterEncodingFilter) {
      // avoid broken umlauts in websphere
      CharacterEncodingFilter filter = new UnknownMimetypeCharacterEncodingFilter();
      filter.setEncoding(StandardCharsets.UTF_8.toString());
      filter.setForceEncoding(true);
      characterEncodingFilter = servletContext.addFilter(CHARACTER_ENCODING_FILTER, filter);
    }
    characterEncodingFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");

    // This filter resolves the current device and adds it to the request
    FilterRegistration deviceResolver = servletContext.getFilterRegistration(DEVICE_RESOLVER_REQUEST_FILTER);
    if(null == deviceResolver) {
      deviceResolver = servletContext.addFilter(DEVICE_RESOLVER_REQUEST_FILTER, new DeviceResolverRequestFilter());
    }
    deviceResolver.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");

    FilterRegistration siteFilter = servletContext.getFilterRegistration(SITE_FILTER);
    if(null == siteFilter) {
      siteFilter = servletContext.addFilter(SITE_FILTER, new DelegatingFilterProxy());
    }
    siteFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/servlet/*");
  }

  @Nonnull
  @Override
  protected Iterable<RegistrationBean> createRegistrationBeans() {
    return Collections.emptyList();
  }
}
