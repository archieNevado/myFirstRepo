package com.coremedia.blueprint.pictures;

import com.coremedia.cap.common.CapSession;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cms.middle.defaultpicture.DefaultPicture;
import com.coremedia.cms.middle.defaultpicture.DefaultPictureService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DefaultPictureLookupStrategy {
  private static final String PREFERRED = "preferred";
  private static final String SECONDARY = "secondary";

  public DefaultPictureLookupStrategy() {}

  public Optional<DefaultPicture> computePicture(List<Content> contents) {
    return computePicture(contents, null);
  }

  public Optional<DefaultPicture> computePicture(List<Content> contents, DefaultPictureService defaultPictureService) {
    if (contents.isEmpty()) {
      return Optional.empty();
    }

    ContentRepository repository = contents.get(0).getRepository();
    CapSession originalSession = repository.getConnection().getConnectionSession().activate();
    try {
      ContentType pictureType = repository.getContentType("CMPicture");
      ContentType teasableType = repository.getContentType("CMTeasable");

      Map<String, List<Content>> separated = contents.stream().collect(Collectors.groupingBy(content -> {
        if(content.isInProduction()) {
          if (pictureType != null && content.getType().isSubtypeOf(pictureType)) {
            return PREFERRED;
          }
          if (teasableType != null && content.getType().isSubtypeOf(teasableType)) {
            return SECONDARY;
          }
        }
        return "";
      }));

      if (separated.containsKey(PREFERRED)) {
        for (Content content : separated.get(PREFERRED)) {
          DefaultPicture defaultPicture = DefaultPictureService.resolveDefaultPictureWithService(defaultPictureService, content);
          if (defaultPicture != null) {
            return Optional.of(defaultPicture);
          }
        }
      }

      if (separated.containsKey(SECONDARY)) {
        for (Content content : separated.get(SECONDARY)) {
          DefaultPicture defaultPicture = DefaultPictureService.resolveDefaultPictureWithService(defaultPictureService, content);
          if (defaultPicture != null) {
            return Optional.of(defaultPicture);
          }
        }
      }
    } finally {
      originalSession.activate();
    }

    return Optional.empty();
  }
}
