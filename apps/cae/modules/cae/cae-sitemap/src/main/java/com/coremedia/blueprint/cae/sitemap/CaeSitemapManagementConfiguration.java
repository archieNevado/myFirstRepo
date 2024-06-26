package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.base.links.UrlPrefixResolver;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.multisite.SitesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

import static java.lang.invoke.MethodHandles.lookup;

@ManagementContextConfiguration(proxyBeanMethods = false)
public class CaeSitemapManagementConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  /**
   * Utility bean, suitable for most sitemap configurations.
   */
  @Bean
  public SitemapIndexRendererFactory sitemapIndexRendererFactory(CaeSitemapConfigurationProperties caeSitemapConfigurationProperties,
                                                                 UrlPrefixResolver ruleUrlPrefixResolver,
                                                                 ObjectProvider<SitemapHelper> sitemapHelperProvider) {
    return new SitemapIndexRendererFactory(
            caeSitemapConfigurationProperties.getTargetRoot(),
            ruleUrlPrefixResolver,
            sitemapHelperProvider);
  }

  /**
   * Site specific configurations, to be populated by extensions.
   */
  @Bean
  public Map<String, SitemapSetup> sitemapConfigurations() {
    return new HashMap<>();
  }

  @Bean
  public SitemapSetupSelector sitemapSetupSelector(@Qualifier("sitemapConfigurations") Map<String, SitemapSetup> sitemapConfigurations,
                                                   SettingsService settingsService,
                                                   SitemapHelper sitemapHelper) {
    return new SitemapSetupSelector(sitemapConfigurations, settingsService, sitemapHelper);
  }

  /**
   * Template for "cronjobs".
   */
  @Bean
  public SitemapGenerationJob sitemapGenerationJobParent(SitemapTrigger sitemapTrigger,
                                                         CaeSitemapConfigurationProperties properties) {
    SitemapGenerationJob generationJob = new SitemapGenerationJob(sitemapTrigger);
    generationJob.setStartTime(properties.getStarttime());
    generationJob.setPeriodMinutes(properties.getPeriodMinutes());
    return generationJob;
  }

  /**
   * Triggers a Sitemap by sending a request to the cae by using the HttpClient.
   */
  @Bean
  public SitemapTriggerImpl sitemapTrigger(SitesService sitesService,
                                           UrlPathFormattingHelper urlPathFormattingHelper,
                                           SitemapSetupSelector sitemapSetupSelector,
                                           ManagementServerProperties managementServerProperties,
                                           ServerProperties serverProperties) {
    Integer port = managementServerProperties.getPort();
    if (port == null) {
      port = serverProperties.getPort();
      if (port == null) {
        port = 80;
      }
      LOG.debug("Management port not set, using port {} instead for sitemap trigger.", port);
    }
    LOG.info("Creating sitemap trigger for port {}.", port);
    return new SitemapTriggerImpl(sitemapSetupSelector, urlPathFormattingHelper, sitesService, port);
  }
}
