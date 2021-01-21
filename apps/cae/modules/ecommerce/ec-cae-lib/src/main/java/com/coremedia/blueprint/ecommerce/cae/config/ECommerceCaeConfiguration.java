package com.coremedia.blueprint.ecommerce.cae.config;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.multisite.cae.SiteResolver;
import com.coremedia.blueprint.cae.handlers.PageActionHandler;
import com.coremedia.blueprint.cae.handlers.PageHandler;
import com.coremedia.blueprint.cae.sitemap.SitemapGenerationHandler;
import com.coremedia.blueprint.ecommerce.cae.AbstractCommerceContextInterceptor;
import com.coremedia.blueprint.ecommerce.cae.WebCommerceContextInterceptor;
import com.coremedia.objectserver.web.MappedInterceptor;
import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@ImportResource(value = {
        "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml",
        "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-cae-services.xml",
        "classpath:/com/coremedia/cap/common/uapi-services.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
@ComponentScan(
        basePackages = {
                "com.coremedia.blueprint.coderesources"
        },
        lazyInit = true
)
public class ECommerceCaeConfiguration {

  @Bean
  public List<String> webCommerceContextInterceptorPatterns() {
    return Lists.newArrayList(PageHandler.SEO_FRIENDLY_URI_PATTERN,
            PageHandler.URI_PATTERN_VANITY,
            PageActionHandler.URI_PATTERN,
            SitemapGenerationHandler.URI_PATTERN);
  }

  @Bean
  public WebCommerceContextInterceptor webCommerceContextInterceptor(SiteResolver siteResolver,
                                                                     CommerceConnectionInitializer commerceConnectionInitializer) {
    WebCommerceContextInterceptor contextInterceptor = new WebCommerceContextInterceptor();

    configureStoreContextInterceptor(contextInterceptor,
            siteResolver,
            commerceConnectionInitializer);

    contextInterceptor.setInitUserContext(true);

    return contextInterceptor;
  }

  /**
   * Duplicates abstract bean storeContextInterceptor in xml config file.
   */
  public static void configureStoreContextInterceptor(AbstractCommerceContextInterceptor contextInterceptor,
                                                      SiteResolver siteResolver,
                                                      CommerceConnectionInitializer commerceConnectionInitializer) {
    contextInterceptor.setSiteResolver(siteResolver);
    contextInterceptor.setCommerceConnectionInitializer(commerceConnectionInitializer);
  }

  @Bean
  public MappedInterceptor mappedWebCommerceContextInterceptor(HandlerInterceptor webCommerceContextInterceptor,
                                                               @Qualifier("webCommerceContextInterceptorPatterns") List<String> webCommerceContextInterceptorPatterns) {
    MappedInterceptor mappedInterceptor = new MappedInterceptor();

    mappedInterceptor.setInterceptor(webCommerceContextInterceptor);
    mappedInterceptor.setIncludePatterns(webCommerceContextInterceptorPatterns);

    return mappedInterceptor;
  }
}
