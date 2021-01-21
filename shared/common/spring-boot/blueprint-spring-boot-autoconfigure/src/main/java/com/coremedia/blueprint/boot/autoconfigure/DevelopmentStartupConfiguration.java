package com.coremedia.blueprint.boot.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * prints the application URL at startup
 */
@Configuration(proxyBeanMethods = false)
@Profile("local")
public class DevelopmentStartupConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(DevelopmentStartupConfiguration.class);

  @Value("${spring.application.name}")
  private String applicationName;

  @Value("http://localhost:${server.port:8080}${server.servlet.context-path:}")
  private String url;

  private final ApplicationContext applicationContext;

  public DevelopmentStartupConfiguration(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @EventListener
  public void handleContextRefresh(ContextRefreshedEvent event) {
    if (!Objects.equals(applicationContext, event.getApplicationContext())) {
      // not my application context
      return;
    }

    LOG.info("{} successfully started at {}", applicationName, url);
  }

}
