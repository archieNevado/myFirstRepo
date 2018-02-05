package com.coremedia.livecontext.ecommerce.ibm.preview;

import com.coremedia.springframework.web.RegistrationBeanBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IbmPreviewCaeConfiguration {

  @Bean
  public FilterRegistrationBean ibmPreviewTokenMarkerFilter() {
    return RegistrationBeanBuilder.forFilter(new IbmPreviewTokenMarkerFilter())
            .order(20_100)
            .build();
  }

  @Bean
  public FilterRegistrationBean wcCookieFilter() {
    return RegistrationBeanBuilder.forFilter(new WcCookieFilter())
            .order(20_110)
            .build();
  }
}
