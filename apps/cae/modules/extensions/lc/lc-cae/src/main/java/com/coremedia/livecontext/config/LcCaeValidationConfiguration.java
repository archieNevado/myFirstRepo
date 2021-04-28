package com.coremedia.livecontext.config;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.livecontext.validation.CMExternalChannelValidator;
import com.coremedia.livecontext.validation.EmptyProductValidator;
import com.coremedia.livecontext.validation.ExternalProductValidator;
import com.coremedia.livecontext.validation.InvalidTeaserTargetValidator;
import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;

@Configuration(proxyBeanMethods = false)
@ImportResource(value = {
        "classpath:/framework/spring/blueprint-services.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
public class LcCaeValidationConfiguration {

  @Customize(value = "contentbeanValidatorList", mode = Customize.Mode.PREPEND)
  @Order(9999999)
  @Bean
  public EmptyProductValidator emptyTargetValidator() {
    return new EmptyProductValidator();
  }

  @Customize(value = "contentbeanValidatorList", mode = Customize.Mode.PREPEND)
  @Order(9999998)
  @Bean
  public CMExternalChannelValidator cmExternalChannelValidator() {
    return new CMExternalChannelValidator();
  }

  @Bean
  @Customize(value = "contentbeanValidatorList", mode = Customize.Mode.PREPEND)
  @Order(9999997)
  public ExternalProductValidator externalProductValidator() {
    return new ExternalProductValidator();
  }

  @Bean
  @Customize(value = "contentbeanValidatorList", mode = Customize.Mode.PREPEND)
  @Order(9999996)
  public InvalidTeaserTargetValidator invalidTeaserTargetValidator(SettingsService settingsService) {
    InvalidTeaserTargetValidator validator = new InvalidTeaserTargetValidator();

    validator.setSettingsService(settingsService);

    return validator;
  }
}
