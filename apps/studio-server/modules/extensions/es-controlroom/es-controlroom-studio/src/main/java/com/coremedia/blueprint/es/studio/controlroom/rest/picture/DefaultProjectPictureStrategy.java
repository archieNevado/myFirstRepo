package com.coremedia.blueprint.es.studio.controlroom.rest.picture;

import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.collaboration.project.Project;
import com.coremedia.collaboration.project.rest.picture.ProjectPictureStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DefaultProjectPictureStrategy implements ProjectPictureStrategy {
  private static final String PREFERRED = "preferred";
  private static final String SECONDARY = "secondary";

  @Override
  public Optional<Blob> computeProjectPicture(Project project) {
    List<Content> contents = new ArrayList<>(project.getContents());

    if (contents.isEmpty()) {
      return Optional.empty();
    }

    ContentRepository repository = contents.get(0).getRepository();

    ContentType pictureType = repository.getContentType("CMPicture");
    ContentType teasableType = repository.getContentType("CMTeasable");

    Map<String, List<Content>> separated = contents.stream().collect(Collectors.groupingBy(content -> {
      if (pictureType != null && content.getType().isSubtypeOf(pictureType)) {
        return PREFERRED;
      }
      if (teasableType != null && content.getType().isSubtypeOf(teasableType)) {
        return SECONDARY;
      }
      return "";
    }));

    if (separated.containsKey(PREFERRED)) {
      for (Content content : separated.get(PREFERRED)) {
        Blob data = content.getBlob("data");
        if (data != null) {
          return Optional.of(data);
        }
      }
    }

    if (separated.containsKey(SECONDARY)) {
      for (Content content : separated.get(SECONDARY)) {
        List<Content> pictures = content.getLinks("pictures");
        if (pictures.isEmpty()) {
          return Optional.empty();
        }
        Blob data = pictures.get(0).getBlob("data");
        if (data != null) {
          return Optional.of(data);
        }
      }
    }

    return Optional.empty();
  }
}
