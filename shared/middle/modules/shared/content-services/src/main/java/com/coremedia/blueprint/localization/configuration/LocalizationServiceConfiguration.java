package com.coremedia.blueprint.localization.configuration;

import com.coremedia.blueprint.localization.BundleResolver;
import com.coremedia.blueprint.localization.ContentBundleResolver;
import com.coremedia.blueprint.localization.LocalResourcesBundleResolver;
import com.coremedia.blueprint.localization.LocalizationService;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.struct.StructService;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.io.File;

@Configuration(proxyBeanMethods = false)
@ImportResource(
        value = {
                "classpath:/com/coremedia/cap/multisite/multisite-services.xml"
        },
        reader = ResourceAwareXmlBeanDefinitionReader.class)
@EnableConfigurationProperties({ThemesConfigurationProperties.class})
public class LocalizationServiceConfiguration {
  private static final Logger LOG = LoggerFactory.getLogger(LocalizationServiceConfiguration.class);

  private final StructService structService;
  private final SitesService sitesService;

  private final File blueprintDir;

  public LocalizationServiceConfiguration(StructService structService,
                                          SitesService sitesService,
                                          ThemesConfigurationProperties properties){
    this.structService = structService;
    this.sitesService = sitesService;
    blueprintDir = initializeBlueprintDir(properties);
  }

  @Nullable
  private static File initializeBlueprintDir(ThemesConfigurationProperties properties) {
    File file = null;
    if (!properties.getProjectDirectory().isEmpty()) {
      file = new File(properties.getProjectDirectory());
      if (!file.exists() || !file.isDirectory() || !file.canRead()) {
        throw new IllegalArgumentException("blueprintPath \"" + properties.getProjectDirectory() + "\" is no suitable directory.");
      }
    }
    return file;
  }

  @Bean(name="localizationService")
  public LocalizationService localizationService(DeliveryConfigurationProperties deliveryConfigurationProperties) {
    BundleResolver bundleResolver = new ContentBundleResolver();
    if (deliveryConfigurationProperties.isLocalResources() && blueprintDir!=null) {
      bundleResolver = new LocalResourcesBundleResolver(bundleResolver, structService, blueprintDir);
      LOG.info("Enabled local resource bundles in {}", blueprintDir.getAbsolutePath());
    }
    return new LocalizationService(structService, sitesService, bundleResolver);
  }
}
