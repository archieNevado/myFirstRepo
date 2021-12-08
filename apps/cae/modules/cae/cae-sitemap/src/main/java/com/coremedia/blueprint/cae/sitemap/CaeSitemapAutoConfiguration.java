package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.blueprint.base.links.UrlPrefixResolver;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cms.delivery.configuration.DeliveryPropertiesAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.lang.invoke.MethodHandles.lookup;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CaeSitemapConfigurationProperties.class)
@AutoConfigureAfter({
        DeliveryPropertiesAutoConfiguration.class,
})
public class CaeSitemapAutoConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  @Bean
  public SitemapHelper sitemapHelper(SettingsService settingsService,
                                     UrlPrefixResolver urlPrefixResolver,
                                     CaeSitemapConfigurationProperties properties,
                                     String deliveryBaseURI) {
    var protocol = properties.getProtocol();
    LOG.info("Creating sitemap helper for base URI '{}' and protocol '{}'.", deliveryBaseURI, protocol);
    return new SitemapHelper(settingsService, urlPrefixResolver, deliveryBaseURI, protocol);
  }

  /**
   * The handler that serves the (generated) sitemaps.
   */
  @Bean
  public SitemapHandler sitemapHandler(CaeSitemapConfigurationProperties configurationProperties,
                                       CapConnection connection) {
    return new SitemapHandler(connection, configurationProperties.getTargetRoot());
  }
}
