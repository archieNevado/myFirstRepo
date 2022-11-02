package com.coremedia.blueprint.es.studio.controlroom.rest;

import com.coremedia.blueprint.es.studio.controlroom.rest.picture.DefaultProjectPictureStrategy;
import com.coremedia.collaboration.project.rest.picture.ProjectPictureStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = {"com.coremedia.workflow.archive"})
public class ESControlRoomStudioConfiguration {

  @Bean
  ProjectPictureStrategy projectPictureStrategy() {
    return new DefaultProjectPictureStrategy();
  }
}
