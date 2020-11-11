package com.coremedia.blueprint.cae.config;

import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.base.links.UrlPrefixResolver;
import com.coremedia.blueprint.base.multisite.cae.SiteResolver;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.sitemap.ContentBasedSitemapSetupFactory;
import com.coremedia.blueprint.cae.sitemap.SitemapGenerationHandler;
import com.coremedia.blueprint.cae.sitemap.SitemapGenerationJob;
import com.coremedia.blueprint.cae.sitemap.SitemapHandler;
import com.coremedia.blueprint.cae.sitemap.SitemapHelper;
import com.coremedia.blueprint.cae.sitemap.SitemapIndexRendererFactory;
import com.coremedia.blueprint.cae.sitemap.SitemapSetup;
import com.coremedia.blueprint.cae.sitemap.SitemapTrigger;
import com.coremedia.blueprint.cae.sitemap.SitemapTriggerImpl;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ImportResource(value = {
        "classpath:/com/coremedia/cae/uapi-services.xml",
        "classpath:/com/coremedia/cae/contentbean-services.xml",
        "classpath:/com/coremedia/cae/link-services.xml",
        "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml",
        "classpath:/com/coremedia/blueprint/base/settings/impl/bpbase-settings-services.xml",
        "classpath:/com/coremedia/blueprint/base/links/bpbase-links-services.xml",
        "classpath:/com/coremedia/blueprint/base/links/bpbase-urlpathformatting.xml",
        "classpath:/framework/spring/blueprint-services.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
@EnableConfigurationProperties({
        BlueprintCaeSitemapConfigurationProperties.class,
        DeliveryConfigurationProperties.class,
})
public class BlueprintSitemapCaeBaseLibConfiguration {

  /**
   * Shared sitemap related features.
   */
  @Bean
  public SitemapHelper sitemapHelper(DeliveryConfigurationProperties deliveryConfigurationProperties,
                                     SettingsService settingsService,
                                     UrlPrefixResolver ruleUrlPrefixResolver,
                                     @Qualifier("sitemapConfigurations") Map<String, SitemapSetup> sitemapConfigurations) {
    SitemapHelper sitemapHelper = new SitemapHelper();

    sitemapHelper.setPrependBaseUri(deliveryConfigurationProperties.isStandalone());
    sitemapHelper.setSettingsService(settingsService);
    sitemapHelper.setUrlPrefixResolver(ruleUrlPrefixResolver);
    sitemapHelper.setSitemapConfigurations(sitemapConfigurations);

    return sitemapHelper;
  }

  /**
   * Utility bean, suitable for most sitemap configurations.
   */
  @Bean
  public SitemapIndexRendererFactory sitemapIndexRendererFactory(BlueprintCaeSitemapConfigurationProperties blueprintCaeSitemapConfigurationProperties,
                                                                 DeliveryConfigurationProperties deliveryConfigurationProperties,
                                                                 UrlPrefixResolver ruleUrlPrefixResolver,
                                                                 ObjectProvider<SitemapHelper> sitemapHelperProvider) {
    return new SitemapIndexRendererFactory(blueprintCaeSitemapConfigurationProperties.getTargetRoot(),
            ruleUrlPrefixResolver,
            sitemapHelperProvider,
            deliveryConfigurationProperties.isStandalone());
  }

  /**
   * The handler that serves the (generated) sitemaps.
   */
  @Bean
  public SitemapHandler sitemapHandler(BlueprintCaeSitemapConfigurationProperties blueprintCaeSitemapConfigurationProperties,
                                       CapConnection connection) {
    SitemapHandler sitemapHandler = new SitemapHandler();

    sitemapHandler.setSitemapDirectory(blueprintCaeSitemapConfigurationProperties.getTargetRoot());
    sitemapHandler.setCapConnection(connection);

    return sitemapHandler;
  }

  /**
   * The handler that generates the sitemaps.
   */
  @Bean
  public SitemapGenerationHandler sitemapGenerationHandler(SiteResolver siteResolver,
                                                           ContentBasedSitemapSetupFactory contentBasedSitemapSetupFactory) {
    SitemapGenerationHandler generationHandler = new SitemapGenerationHandler();

    generationHandler.setSiteResolver(siteResolver);
    generationHandler.setSitemapSetupFactory(contentBasedSitemapSetupFactory);

    return generationHandler;
  }

  /**
   * SitemapSetupFactory which uses the settings "sitemapOrgConfiguration" from the site.
   */
  @Bean
  public ContentBasedSitemapSetupFactory contentBasedSitemapSetupFactory(SitemapHelper sitemapHelper) {
    ContentBasedSitemapSetupFactory setupFactory = new ContentBasedSitemapSetupFactory();

    setupFactory.setSitemapHelper(sitemapHelper);

    return setupFactory;
  }

  /**
   * Site specific configurations, to be populated by extensions.
   */
  @Bean
  public Map<String, SitemapSetup> sitemapConfigurations() {
    return new HashMap<>();
  }

  /**
   * Template for "cronjobs".
   */
  @Bean
  public SitemapGenerationJob sitemapGenerationJobParent(SitemapTrigger sitemapTrigger,
                                                         BlueprintCaeSitemapConfigurationProperties properties) {
    SitemapGenerationJob generationJob = new SitemapGenerationJob();

    generationJob.setStartTime(properties.getStarttime());
    generationJob.setPeriodMinutes(properties.getPeriodMinutes());
    generationJob.setSitemapTrigger(sitemapTrigger);

    return generationJob;
  }

  /**
   * Triggers a Sitemap by sending a request to the cae by using the HttpClient.
   */
  @Bean
  public SitemapTriggerImpl sitemapTrigger(SitesService sitesService,
                                           UrlPathFormattingHelper urlPathFormattingHelper,
                                           SitemapHelper sitemapHelper,
                                           BlueprintCaeSitemapConfigurationProperties blueprintCaeSitemapConfigurationProperties) {
    SitemapTriggerImpl trigger = new SitemapTriggerImpl();

    trigger.setMyOwnPort(blueprintCaeSitemapConfigurationProperties.getCaePort());
    trigger.setSitesService(sitesService);
    trigger.setUrlPathFormattingHelper(urlPathFormattingHelper);
    trigger.setSitemapHelper(sitemapHelper);

    return trigger;
  }
}
