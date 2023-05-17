package com.coremedia.blueprint.workflows.studio.rest.picture;

import com.coremedia.blueprint.pictures.DefaultPictureLookupStrategy;
import com.coremedia.cap.common.CapPropertyDescriptor;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.workflow.Process;
import com.coremedia.cms.middle.defaultpicture.DefaultPicture;
import com.coremedia.cms.middle.defaultpicture.DefaultPictureResolverWithService;
import com.coremedia.cms.middle.defaultpicture.DefaultPictureService;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.cap.common.CapPropertyDescriptorType.LINK;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

public class ProcessDefaultPictureResolver implements DefaultPictureResolverWithService<Process> {

  private static final Logger LOG = getLogger(lookup().lookupClass());

  private static final String CHANGE_SET = "changeSet";
  private static final String MASTER_CONTENT_OBJECTS = "masterContentObjects";
  private static final List<String> CHANGE_SET_PROPERTIES = Arrays.asList(CHANGE_SET, MASTER_CONTENT_OBJECTS);

  private final DefaultPictureLookupStrategy lookupStrategy;

  public ProcessDefaultPictureResolver(DefaultPictureLookupStrategy lookupStrategy) {
    this.lookupStrategy = lookupStrategy;
  }

  @NonNull
  @Override
  public Optional<DefaultPicture> resolve(@NonNull Process process, @NonNull DefaultPictureService defaultPictureService) {
    try {
      Map<String, CapPropertyDescriptor> descriptorsByName = process.getDefinition().getDescriptorsByName();
      for (String changeSetProperty : CHANGE_SET_PROPERTIES) {
        if(descriptorsByName.containsKey(changeSetProperty)) {
          CapPropertyDescriptor changeSetDescriptor = process.getDefinition().getDescriptor(changeSetProperty);
          if (changeSetDescriptor != null && changeSetDescriptor.getType() == LINK) {
            List<Content> links = process.getLinks(changeSetProperty);
            return lookupStrategy.computePicture(new ArrayList<>(links), defaultPictureService);
          }
        }
      }
    } catch (Exception e) {
      LOG.error("Workflow default picture lookup failed: {}", e.getMessage(), e);
    }

    return Optional.empty();
  }
}
