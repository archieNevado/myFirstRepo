package com.coremedia.blueprint.studio.blueprint.component.boot;

import com.coremedia.collaboration.notifications.WorkflowNotificationsConfiguration;
import com.coremedia.collaboration.userchanges.UserChangesConfiguration;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.PostConstruct;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "elastic.core.persistence", havingValue = "memory")
@ImportResource(value = {
        "classpath:/META-INF/coremedia/studio-in-memory-cap-list.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
@Import({
        WorkflowNotificationsConfiguration.class,
        UserChangesConfiguration.class})
public class StudioInMemoryConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(StudioInMemoryConfiguration.class);

  @PostConstruct
  void initialize() {
    LOG.info("Initializing in-memory configuration for studio-webapp.");
  }
}
