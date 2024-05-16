package com.coremedia.blueprint.assets.studio.validation;

import com.coremedia.blueprint.assets.AssetManagementConfigurationProperties;
import com.coremedia.blueprint.base.config.ConfigurationService;
import com.coremedia.blueprint.base.config.ConfigurationServiceConfiguration;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.undoc.common.spring.CapRepositoriesConfiguration;
import com.coremedia.rest.cap.validation.ContentTypeValidator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Objects;

@Configuration(proxyBeanMethods = false)
@Import({
        CapRepositoriesConfiguration.class,
        ConfigurationServiceConfiguration.class,
})
@EnableConfigurationProperties({
        AssetManagementConfigurationProperties.class
})
public class AssetValidatorsConfiguration {

  /**
   * Validator for AMAsset content.
   */
  @Bean
  ContentTypeValidator amAssetValidator(ConfigurationService configurationService,
                                        CapConnection connection,
                                        AssetManagementConfigurationProperties assetManagementConfigurationProperties) {
    AssetMetadataValidator assetMetadataValidator = new AssetMetadataValidator();
    assetMetadataValidator.setMetadataProperty("metadata");
    assetMetadataValidator.setConfigurationService(configurationService);
    assetMetadataValidator.setSettingsDocument(assetManagementConfigurationProperties.getSettingsDocument());

    ContentType amAsset = Objects.requireNonNull(connection.getContentRepository().getContentType("AMAsset"));
    ContentTypeValidator contentTypeValidator = new ContentTypeValidator(amAsset, true, List.of(assetMetadataValidator));

    return contentTypeValidator;
  }

}
