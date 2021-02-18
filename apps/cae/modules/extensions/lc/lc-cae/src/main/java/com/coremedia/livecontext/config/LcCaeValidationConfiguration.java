package com.coremedia.livecontext.config;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.services.validation.Validator;
import com.coremedia.livecontext.validation.CMExternalChannelValidator;
import com.coremedia.livecontext.validation.EmptyProductValidator;
import com.coremedia.livecontext.validation.ExternalProductValidator;
import com.coremedia.livecontext.validation.InvalidTeaserTargetValidator;
import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
@ImportResource(value = {
        "classpath:/framework/spring/blueprint-services.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
public class LcCaeValidationConfiguration {

  @Bean(autowireCandidate = false)
  @Customize(value = "contentbeanValidatorList", mode = Customize.Mode.PREPEND)
  @Order(9999999)
  public List<Validator> addLiveContextValidators(EmptyProductValidator emptyTargetValidator,
                                                  CMExternalChannelValidator cmExternalChannelValidator,
                                                  ExternalProductValidator externalProductValidator,
                                                  InvalidTeaserTargetValidator invalidTeaserTargetValidator) {
    return Lists.newArrayList(emptyTargetValidator,
            cmExternalChannelValidator,
            externalProductValidator,
            invalidTeaserTargetValidator);
  }

  @Bean
  public EmptyProductValidator emptyTargetValidator() {
    return new EmptyProductValidator();
  }

  @Bean
  public CMExternalChannelValidator cmExternalChannelValidator() {
    return new CMExternalChannelValidator();
  }

  @Bean
  public ExternalProductValidator externalProductValidator() {
    return new ExternalProductValidator();
  }

  @Bean
  public InvalidTeaserTargetValidator invalidTeaserTargetValidator(SettingsService settingsService) {
    InvalidTeaserTargetValidator validator = new InvalidTeaserTargetValidator();

    validator.setSettingsService(settingsService);

    return validator;
  }
}
