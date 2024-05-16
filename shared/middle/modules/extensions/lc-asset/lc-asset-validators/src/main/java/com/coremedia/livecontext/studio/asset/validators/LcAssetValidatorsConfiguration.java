package com.coremedia.livecontext.studio.asset.validators;

import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.undoc.common.spring.CapRepositoriesConfiguration;
import com.coremedia.livecontext.asset.util.AssetReadSettingsHelper;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.util.Objects;

@Configuration(proxyBeanMethods = false)
@Import(CapRepositoriesConfiguration.class)
@ImportResource(value = {
        "classpath:/framework/spring/lc-asset-helpers.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
public class LcAssetValidatorsConfiguration {

  private static final String CMSPINNER_NAME = "CMSpinner";

  @Bean
  SpinnerSequenceAssetValidator spinnerSequenceAssetValidator(AssetReadSettingsHelper assetHelper,
                                                              CapConnection connection) {
    ContentType type = Objects.requireNonNull(connection.getContentRepository().getContentType(CMSPINNER_NAME));
    return new SpinnerSequenceAssetValidator(type, true, assetHelper);
  }

}
