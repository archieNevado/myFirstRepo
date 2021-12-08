package com.coremedia.blueprint.boot.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

import java.util.Objects;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * prints the application URL at startup
 */
@Configuration(proxyBeanMethods = false)
@Profile("local")
public class DevelopmentStartupAutoConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  @Value("${spring.application.name}")
  private String applicationName;

  @Value("http://localhost:${server.port:8080}${server.servlet.context-path:}")
  private String url;

  private final ApplicationContext applicationContext;

  public DevelopmentStartupAutoConfiguration(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @EventListener
  public void applicationStarted(ApplicationStartedEvent event) {
    if (!Objects.equals(applicationContext, event.getApplicationContext())) {
      // not my application context
      return;
    }

    LOG.info("{} successfully started at {}", applicationName, url);
  }

}
