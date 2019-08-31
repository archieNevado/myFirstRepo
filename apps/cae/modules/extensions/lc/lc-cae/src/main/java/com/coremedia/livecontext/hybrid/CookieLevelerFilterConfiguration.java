package com.coremedia.livecontext.hybrid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

@Configuration
public class CookieLevelerFilterConfiguration {
  private static final Logger LOGGER = LoggerFactory.getLogger(CookieLevelerFilterConfiguration.class);

  @Autowired
  private Environment environment;

  @Bean
  @ConditionalOnProperty(name = "livecontext.cookie.domain")
  CookieLevelerFilter cookieLevelerFilter() {
    return new CookieLevelerFilter();
  }

  @PostConstruct
  void initialize() {
    String property = environment.getProperty("livecontext.cookie.domain");
    if (property == null) {
      LOGGER.info("Cookie Leveler Filter is disabled.");
    } else {
      LOGGER.info("Cookie Leveler Filter is enabled. Configured domains: {}", property);
    }
  }
}
