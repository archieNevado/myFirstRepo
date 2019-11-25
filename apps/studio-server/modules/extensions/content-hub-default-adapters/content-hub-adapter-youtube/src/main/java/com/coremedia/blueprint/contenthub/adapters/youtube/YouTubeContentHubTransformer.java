package com.coremedia.blueprint.contenthub.adapters.youtube;

import com.coremedia.cap.common.Blob;
import com.coremedia.contenthub.api.ContentCreationUtil;
import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubContext;
import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubTransformer;
import com.coremedia.contenthub.api.ContentModel;
import com.coremedia.contenthub.api.ContentModelReference;
import com.coremedia.contenthub.api.Item;
import com.coremedia.contenthub.api.MimeTypeFactory;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

public class YouTubeContentHubTransformer implements ContentHubTransformer {

  private ContentCreationUtil contentCreationUtil;

  YouTubeContentHubTransformer(ContentCreationUtil contentCreationUtil) {
    this.contentCreationUtil = contentCreationUtil;
  }

  @NonNull
  @Override
  public ContentModel transform(Item item, ContentHubAdapter contentHubAdapter, ContentHubContext contentHubContext) {
    if (!(item instanceof YouTubeItem)) {
      throw new IllegalArgumentException("Cannot handle " + item);
    }

    YouTubeItem video = (YouTubeItem) item;
    ContentModel contentModel = new ContentModel(item.getDisplayName(), item.getId());
    contentModel.put("title", item.getName());

    //put URL
    String videoUrl = "https://www.youtube.com/watch?v=" + video.getVideo().getId();
    contentModel.put("dataUrl", videoUrl);

    //put description
    String description = video.getDescription();
    if (!StringUtils.isEmpty(description)) {
      contentModel.put("detailText", contentCreationUtil.convertStringToMarkup(description));
    }

    //store image references
    String url = thumbnailUrl(video.getVideo());
    if (url != null) {
      ContentModelReference ref = ContentModelReference.create(contentModel, "CMPicture", url);
      contentModel.put("pictures", Collections.singletonList(ref));
    }

    return contentModel;
  }

  @Override
  @Nullable
  public ContentModel resolveReference(ContentModelReference reference, ContentHubAdapter contentHubAdapter, ContentHubContext contentHubContext) {
    Object data = reference.getData();
    if (!(data instanceof String)) {
      throw new IllegalArgumentException("Not my reference: " + reference);
    }

    String imageUrl = (String) data;
    String imageName = reference.getOwner().getContentName() + " (Thumbnail)";
    ContentHubObjectId referenceId = ContentHubObjectId.createReference(reference.getOwner().getContentHubObjectId(), imageName);

    ContentModel contentModel = new ContentModel(imageName, referenceId);
    Blob pictureBlob = contentCreationUtil.createPictureFromUrl(imageUrl, "Image " + imageName, MimeTypeFactory.create("image/jpeg"));
    contentModel.put("data", pictureBlob);
    contentModel.put("title", "YouTube Thumbnail " + imageName);

    return contentModel;
  }

  @Override
  public boolean isApplicable(ContentHubObject contentHubObject) {
    return contentHubObject instanceof YouTubeHubObject;
  }

  @Nullable
  private static String thumbnailUrl(@Nullable Video video) {
    VideoSnippet snippet = video!=null ? video.getSnippet() : null;
    ThumbnailDetails thumbnails = snippet!=null ? snippet.getThumbnails() : null;
    if (thumbnails != null) {
      if (thumbnails.getMaxres() != null) {
        return thumbnails.getMaxres().getUrl();
      }
      if (thumbnails.getHigh() != null) {
        return thumbnails.getHigh().getUrl();
      }
      if (thumbnails.getDefault() != null) {
        return thumbnails.getDefault().getUrl();
      }
    }
    return null;
  }
}
