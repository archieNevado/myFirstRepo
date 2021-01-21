package com.coremedia.ecommerce.studio.rest.filter;

import com.coremedia.cap.multisite.SitesService;
import com.coremedia.springframework.boot.web.servlet.RegistrationBeanBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.Filter;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class EcStudioFilters {

  private static final String SITE_FILTER = "siteFilter";

  @Bean
  public FilterRegistrationBean siteFilterRegistration(SitesService sitesService) {
    return RegistrationBeanBuilder
            .forFilter(new SiteFilter(sitesService))
            .name(SITE_FILTER)
            .urlPatterns("/api/livecontext/*")
            .order(900)
            .build();
  }

  @Bean
  public CatalogResourceEncodingFilter catalogResourceEncodingFilter() {
    List<RequestMatcher> includeRequestMatchers = List.of(
            new AntPathRequestMatcher("/api/livecontext/category/**"),
            new AntPathRequestMatcher("/api/livecontext/product/**"),
            new AntPathRequestMatcher("/api/livecontext/sku/**")
    );

    return new CatalogResourceEncodingFilter(includeRequestMatchers);
  }
}
