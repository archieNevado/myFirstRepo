package com.coremedia.blueprint.pictures;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DefaultPictureLookupConfiguration {

  @Bean
  DefaultPictureLookupStrategy defaultPictureLookupStrategy() {
    return new DefaultPictureLookupStrategy();
  }
}
