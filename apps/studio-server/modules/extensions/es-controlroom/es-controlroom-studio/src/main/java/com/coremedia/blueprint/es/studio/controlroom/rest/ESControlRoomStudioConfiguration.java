package com.coremedia.blueprint.es.studio.controlroom.rest;

import com.coremedia.blueprint.es.studio.controlroom.rest.picture.DefaultProjectPictureStrategy;
import com.coremedia.blueprint.pictures.DefaultPictureLookupAutoConfiguration;
import com.coremedia.blueprint.pictures.DefaultPictureLookupStrategy;
import com.coremedia.collaboration.project.rest.picture.ProjectPictureStrategy;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ComponentScan(basePackages = {"com.coremedia.workflow.archive"})
@Import({
        DefaultPictureLookupAutoConfiguration.class
})
public class ESControlRoomStudioConfiguration {

  @Bean
  ProjectPictureStrategy projectPictureStrategy(DefaultPictureLookupStrategy lookupStrategy) {
    return new DefaultProjectPictureStrategy(lookupStrategy);
  }
}
