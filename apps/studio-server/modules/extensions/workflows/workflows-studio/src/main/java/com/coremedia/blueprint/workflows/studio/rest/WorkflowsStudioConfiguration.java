package com.coremedia.blueprint.workflows.studio.rest;

import com.coremedia.blueprint.pictures.DefaultPictureLookupAutoConfiguration;
import com.coremedia.blueprint.pictures.DefaultPictureLookupStrategy;
import com.coremedia.blueprint.workflows.studio.rest.picture.ProcessDefaultPictureResolver;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
        DefaultPictureLookupAutoConfiguration.class
})
public class WorkflowsStudioConfiguration {

  @Bean
  public ProcessDefaultPictureResolver processDefaultPictureResolver(DefaultPictureLookupStrategy defaultPictureLookupStrategy) {
    return new ProcessDefaultPictureResolver(defaultPictureLookupStrategy);
  }
}
