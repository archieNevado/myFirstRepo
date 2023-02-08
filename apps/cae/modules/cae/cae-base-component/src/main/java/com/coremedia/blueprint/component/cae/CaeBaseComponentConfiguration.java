package com.coremedia.blueprint.component.cae;

import com.coremedia.blueprint.base.multisite.cae.SiteResolver;
import com.coremedia.blueprint.cae.filter.PreviewViewFilter;
import com.coremedia.blueprint.cae.filter.RequestRejectedExceptionFilter;
import com.coremedia.blueprint.cae.filter.SiteFilter;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.ConditionalOnMissingFilterBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.RequestContextFilter;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        DeliveryConfigurationProperties.class,
})
public class CaeBaseComponentConfiguration {

  public static final int ORDER_SITE_FILTER = 100;

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
