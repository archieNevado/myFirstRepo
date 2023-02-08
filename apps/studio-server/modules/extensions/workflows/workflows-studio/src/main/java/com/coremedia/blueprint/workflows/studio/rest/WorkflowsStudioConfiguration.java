package com.coremedia.blueprint.workflows.studio.rest;

import com.coremedia.blueprint.pictures.DefaultPictureLookupAutoConfiguration;
import com.coremedia.blueprint.pictures.DefaultPictureLookupStrategy;
import com.coremedia.blueprint.workflows.studio.rest.picture.DefaultWorkflowPictureStrategy;
import com.coremedia.rest.cap.workflow.pictures.WorkflowPictureStrategy;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
        DefaultPictureLookupAutoConfiguration.class
})
public class WorkflowsStudioConfiguration {

  @Bean
  WorkflowPictureStrategy workflowPictureStrategy(DefaultPictureLookupStrategy lookupStrategy) {
    return new DefaultWorkflowPictureStrategy(lookupStrategy);
  }
}
