package com.coremedia.livecontext.cae;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.NoStoreContextAvailable;
import com.coremedia.blueprint.cae.exception.handler.SimpleExceptionHandler;
import com.coremedia.cae.security.CaeCsrfIgnoringRequestMatcher;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.hybrid.CookieLevelerFilter;
import com.coremedia.livecontext.search.CommerceSearchCsrfIgnoringRequestMatcher;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = {
        "com.coremedia.livecontext.web",
})
@ImportResource(value = {
        "classpath:/META-INF/coremedia/calista-handler.xml",
        "classpath:/META-INF/coremedia/livecontext-cae-services.xml",
        "classpath:/META-INF/coremedia/livecontext-contentbeans.xml",
        "classpath:/META-INF/coremedia/livecontext-fragment.xml",
        "classpath:/META-INF/coremedia/livecontext-freemarker-views.xml",
        "classpath:/META-INF/coremedia/livecontext-handler-interceptors.xml",
        "classpath:/META-INF/coremedia/livecontext-handlers.xml",
        "classpath:/META-INF/coremedia/livecontext-links.xml",
        "classpath:/META-INF/coremedia/livecontext-resolver.xml",
        "classpath:/META-INF/coremedia/livecontext-validation.xml",
        "classpath:/META-INF/coremedia/livecontext-views.xml",
        "classpath:/framework/spring/errorhandling.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
public class LcCaeAutoConfiguration {

  @Bean
  FragmentContextProvider fragmentContextProvider() {
    return new FragmentContextProvider();
  }

  @Bean
  @ConditionalOnProperty(name = "livecontext.cookie.domain")
  static CookieLevelerFilter cookieLevelerFilter() {
    return new CookieLevelerFilter();
  }

  @Bean
  @Order(10)
  SimpleExceptionHandler<NoStoreContextAvailable> noStoreContextAvailableSimpleExceptionHandler() {
    SimpleExceptionHandler<NoStoreContextAvailable> exceptionHandler = new SimpleExceptionHandler<>();
    exceptionHandler.setExceptionType(NoStoreContextAvailable.class);
    exceptionHandler.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
    return exceptionHandler;
  }

  @Bean
  @Order(100)
  SimpleExceptionHandler<CommerceException> commerceExceptionSimpleExceptionHandler() {
    SimpleExceptionHandler<CommerceException> exceptionHandler = new SimpleExceptionHandler<>();
    exceptionHandler.setExceptionType(CommerceException.class);
    exceptionHandler.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    return exceptionHandler;
  }

  @Bean
  CaeCsrfIgnoringRequestMatcher caeCsrfIgnoringRequestMatcher() {
    return new CommerceSearchCsrfIgnoringRequestMatcher();
  }
}
