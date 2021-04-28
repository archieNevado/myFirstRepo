package com.coremedia.blueprint.cae.config;

import com.coremedia.blueprint.cae.web.i18n.LinklistPageResourceBundleFactory;
import com.coremedia.blueprint.cae.web.i18n.RequestLocaleResolver;
import com.coremedia.blueprint.cae.web.i18n.RequestMessageSource;
import com.coremedia.blueprint.cae.web.i18n.ResourceBundleInterceptor;
import com.coremedia.blueprint.coderesources.ThemeService;
import com.coremedia.blueprint.localization.LocalizationService;
import com.coremedia.blueprint.localization.configuration.LocalizationServiceConfiguration;
import com.coremedia.cache.Cache;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;

/**
 * Internationalization (i18n) features
 */
@Configuration(proxyBeanMethods = false)
@ImportResource(value = {
        "classpath:/com/coremedia/cae/handler-services.xml",
        "classpath:/com/coremedia/cache/cache-services.xml",
        "classpath:/com/coremedia/cap/multisite/multisite-services.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
@Import({LocalizationServiceConfiguration.class})
@ComponentScan(
        basePackages = {
                "com.coremedia.blueprint.coderesources"
        },
        lazyInit = true
)
/*
 * Internationalization (i18n) features
 */
public class BlueprintI18nCaeBaseLibConfiguration {

  @Bean(name={"linklistPageResourceBundleFactory", "pageResourceBundleFactory"})
  public LinklistPageResourceBundleFactory linklistPageResourceBundleFactory(Cache cache,
                                                                             SitesService sitesService,
                                                                             LocalizationService localizationService,
                                                                             ThemeService themeService,
                                                                             DeliveryConfigurationProperties deliveryConfigurationProperties) {
    LinklistPageResourceBundleFactory factory = new LinklistPageResourceBundleFactory();
    factory.setCache(cache);
    factory.setUseLocalresources(deliveryConfigurationProperties.isLocalResources());
    factory.setSitesService(sitesService);
    factory.setLocalizationService(localizationService);
    factory.setThemeService(themeService);
    return factory;
  }

  /**
   * Makes a Page's resource bundle available to &lt;spring:message&gt; and &lt;fmt:message&gt;
   *
   * This interceptor's #postHandle method should be invoked
   * quite early so that other interceptors can make use of the registered message source. This
   * can be achieved by putting the other interceptors IN FRONT of this one, i.e by prepending them.
   */
  @Bean
  @Customize(value = "handlerInterceptors", mode = Customize.Mode.APPEND)
  @Order(10000)
  public ResourceBundleInterceptor pageResourceBundlesInterceptor(LinklistPageResourceBundleFactory pageResourceBundleFactory) {
    ResourceBundleInterceptor interceptor = new ResourceBundleInterceptor();
    interceptor.setResourceBundleFactory(pageResourceBundleFactory);
    return interceptor;
  }

  /**
   * A special message source that is able to delegate to a message source that is stored in the current request.
   */
  @Bean
  public RequestMessageSource messageSource() {
    RequestMessageSource messageSource = new RequestMessageSource();

    //A parent message source can be injected here like this.
    //messageSource.setParentMessageSource(yourMessageSource);

    return messageSource;
  }

  /**
   * Enables modifying the current locale per request, but does not store cookie or create a session.
   */
  @Bean
  public RequestLocaleResolver localeResolver() {
    return new RequestLocaleResolver();
  }
}
