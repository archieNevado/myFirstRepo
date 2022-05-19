package com.coremedia.blueprint.localization.configuration;

import com.coremedia.blueprint.base.multisite.BlueprintMultisiteConfiguration;
import com.coremedia.blueprint.localization.BundleResolver;
import com.coremedia.blueprint.localization.ContentBundleResolver;
import com.coremedia.blueprint.localization.LocalResourcesBundleResolver;
import com.coremedia.blueprint.localization.LocalizationService;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.struct.StructService;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({
        BlueprintMultisiteConfiguration.class,
})
@EnableConfigurationProperties({
        DeliveryConfigurationProperties.class,
})
public class LocalizationServiceConfiguration {
  private static final Logger LOG = LoggerFactory.getLogger(LocalizationServiceConfiguration.class);

  private final StructService structService;
  private final SitesService sitesService;

  public LocalizationServiceConfiguration(StructService structService, SitesService sitesService) {
    this.structService = structService;
    this.sitesService = sitesService;
  }

  @Bean(name="localizationService")
  public LocalizationService localizationService(DeliveryConfigurationProperties deliveryConfigurationProperties,
                                                 ApplicationContext applicationContext) {
    BundleResolver bundleResolver = new ContentBundleResolver();
    if (deliveryConfigurationProperties.isLocalResources()) {
      bundleResolver = new LocalResourcesBundleResolver(bundleResolver, structService, applicationContext);
      LOG.info("Enabled local resource bundles.");
    }
    return new LocalizationService(structService, sitesService, bundleResolver);
  }
}
