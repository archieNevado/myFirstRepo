package com.coremedia.blueprint.workflows.studio.rest.picture;

import com.coremedia.blueprint.pictures.DefaultPictureLookupStrategy;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.CapPropertyDescriptor;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.workflow.Process;
import com.coremedia.rest.cap.workflow.pictures.WorkflowPictureStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.cap.common.CapPropertyDescriptorType.LINK;

public class DefaultWorkflowPictureStrategy implements WorkflowPictureStrategy {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String CHANGE_SET = "changeSet";
  private static final String MASTER_CONTENT_OBJECTS = "masterContentObjects";
  private static final List<String> CHANGE_SET_PROPERTIES = Arrays.asList(CHANGE_SET, MASTER_CONTENT_OBJECTS);

  private final DefaultPictureLookupStrategy lookupStrategy;

  public DefaultWorkflowPictureStrategy(DefaultPictureLookupStrategy lookupStrategy) {
    this.lookupStrategy = lookupStrategy;
  }

  @Override
  public Optional<Blob> computeWorkflowPicture(Process process) {
    try {
      Map<String, CapPropertyDescriptor> descriptorsByName = process.getDefinition().getDescriptorsByName();
      for (String changeSetProperty : CHANGE_SET_PROPERTIES) {
        if(descriptorsByName.containsKey(changeSetProperty)) {
          CapPropertyDescriptor changeSetDescriptor = process.getDefinition().getDescriptor(changeSetProperty);
          if (changeSetDescriptor != null && changeSetDescriptor.getType() == LINK) {
            List<Content> links = process.getLinks(changeSetProperty);
            return lookupStrategy.computePicture(new ArrayList<>(links));
          }
        }
      }
    } catch (Exception e) {
      LOG.error("Workflow default picture lookup failed: {}", e.getMessage(), e);
    }

    return Optional.empty();
  }
}
