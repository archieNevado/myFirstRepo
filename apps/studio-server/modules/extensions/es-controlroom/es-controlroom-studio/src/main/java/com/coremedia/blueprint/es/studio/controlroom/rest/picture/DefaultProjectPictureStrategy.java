package com.coremedia.blueprint.es.studio.controlroom.rest.picture;

import com.coremedia.blueprint.pictures.DefaultPictureLookupStrategy;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.collaboration.project.Project;
import com.coremedia.collaboration.project.rest.picture.ProjectPictureStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultProjectPictureStrategy implements ProjectPictureStrategy {

  private final DefaultPictureLookupStrategy lookupStrategy;

  public DefaultProjectPictureStrategy(DefaultPictureLookupStrategy lookupStrategy) {
    this.lookupStrategy = lookupStrategy;
  }

  @Override
  public Optional<Blob> computeProjectPicture(Project project) {
    List<Content> contents = new ArrayList<>(project.getContents());
    return lookupStrategy.computePicture(contents);
  }
}
