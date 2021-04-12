package com.coremedia.blueprint.component.cae;

import com.coremedia.blueprint.base.multisite.cae.SiteResolver;
import com.coremedia.blueprint.cae.filter.PreviewViewFilter;
import com.coremedia.blueprint.cae.filter.RequestRejectedExceptionFilter;
import com.coremedia.blueprint.cae.filter.SiteFilter;
import com.coremedia.blueprint.cae.filter.UnknownMimetypeCharacterEncodingFilter;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.RequestContextFilter;

import java.nio.charset.StandardCharsets;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        DeliveryConfigurationProperties.class,
})
public class CaeBaseComponentConfiguration {

  public static final int ORDER_SITE_FILTER = 100;
  private static final int ORDER_MIMETYPE_FILTER = Ordered.HIGHEST_PRECEDENCE + 1_147_483_648; // == -1_000_000_000

  @Bean
  @ConditionalOnProperty(value = "cae.set-unknown-mime-type")
  public FilterRegistrationBean<UnknownMimetypeCharacterEncodingFilter> characterEncodingFilterRegistration() {
    var filter = new UnknownMimetypeCharacterEncodingFilter();
    filter.setEncoding(StandardCharsets.UTF_8.toString());
    filter.setForceEncoding(true);
    var registrationBean = new FilterRegistrationBean<>(filter);
    registrationBean.setOrder(ORDER_MIMETYPE_FILTER);
    return registrationBean;
  }

  // reset request attributes (copied from org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter)
  @Bean
  @ConditionalOnMissingBean({RequestContextListener.class, RequestContextFilter.class})
  @ConditionalOnMissingFilterBean(RequestContextFilter.class)
  public static RequestContextFilter requestContextFilter() {
    return new OrderedRequestContextFilter();
  }

  @Bean
  public FilterRegistrationBean<SiteFilter> siteFilterRegistration(SiteResolver siteResolver) {
    var siteFilter = new SiteFilter();
    siteFilter.setSiteResolver(siteResolver);
    var registrationBean = new FilterRegistrationBean<>(siteFilter);
    registrationBean.setOrder(ORDER_SITE_FILTER);
    registrationBean.addUrlPatterns("/servlet/*");
    return registrationBean;
  }

  /**
   * Rejects preview specific requests on Live CAEs.
   */
  @Bean
  public PreviewViewFilter previewViewFilter(DeliveryConfigurationProperties properties) {
    return new PreviewViewFilter(!properties.isPreviewMode());
  }

  @Bean
  public FilterRegistrationBean<RequestRejectedExceptionFilter> requestRejectedExceptionFilterRegistration() {
    var requestRejectedExceptionFilter = new RequestRejectedExceptionFilter();
    var registrationBean = new FilterRegistrationBean<>(requestRejectedExceptionFilter);
    registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return registrationBean;
  }
}
