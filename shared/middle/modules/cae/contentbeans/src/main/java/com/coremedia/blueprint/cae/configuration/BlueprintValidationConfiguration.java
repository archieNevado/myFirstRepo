package com.coremedia.blueprint.cae.configuration;

import com.coremedia.blueprint.cae.services.validation.ValidationServiceImpl;
import com.coremedia.blueprint.common.datevalidation.ValidUntilConsumer;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.blueprint.common.services.validation.Validator;
import com.coremedia.cache.Cache;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.springframework.customizer.Customize;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.List;

@Configuration
@DefaultAnnotation(NonNull.class)
public class BlueprintValidationConfiguration {
  /**
   * list bean of Validator implementations used by the validationService
   */
  @Bean
  List<Validator> contentbeanValidatorList(DeliveryConfigurationProperties deliveryConfigurationProperties,
                                           ObjectProvider<ValidUntilConsumer> validUntilConsumersProvider) {
    return List.of(validityPeriodValidator(deliveryConfigurationProperties, validUntilConsumersProvider));
  }

  @Customize("solrSearchFilters")
  @Bean
  ValidityPeriodValidator validityPeriodValidator(DeliveryConfigurationProperties deliveryConfigurationProperties,
                                                  ObjectProvider<ValidUntilConsumer> validUntilConsumersProvider) {
    return new ValidityPeriodValidator(deliveryConfigurationProperties, validUntilConsumersProvider);
  }

  @Bean
  ValidationService validationService(@Qualifier("contentbeanValidatorList") List<Validator> validators) {
    ValidationServiceImpl validationService = new ValidationServiceImpl();
    validationService.setValidators(validators);
    return validationService;
  }

  @Bean
  ValidUntilConsumer cacheUntilExpired() {
    return instant -> Cache.cacheUntil(Date.from(instant));
  }
}
