package com.coremedia.blueprint.es.studio.controlroom.rest.picture;

import com.coremedia.blueprint.pictures.DefaultPictureLookupStrategy;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.middle.defaultpicture.DefaultPicture;
import com.coremedia.cms.middle.defaultpicture.DefaultPictureResolverWithService;
import com.coremedia.cms.middle.defaultpicture.DefaultPictureService;
import com.coremedia.collaboration.project.Project;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectDefaultPictureResolver implements DefaultPictureResolverWithService<Project> {

  private final DefaultPictureLookupStrategy lookupStrategy;

  public ProjectDefaultPictureResolver(DefaultPictureLookupStrategy lookupStrategy) {
    this.lookupStrategy = lookupStrategy;
  }

  @NonNull
  @Override
  public Optional<DefaultPicture> resolve(@NonNull Project entity, @NonNull DefaultPictureService defaultPictureService) {
    List<Content> contents = new ArrayList<>(entity.getContents());
    return lookupStrategy.computePicture(contents, defaultPictureService);
  }
}
