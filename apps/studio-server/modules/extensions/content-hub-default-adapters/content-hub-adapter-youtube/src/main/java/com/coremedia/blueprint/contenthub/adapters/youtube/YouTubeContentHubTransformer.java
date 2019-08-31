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
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class YouTubeContentHubTransformer implements ContentHubTransformer {

  private ContentCreationUtil contentCreationUtil;

  YouTubeContentHubTransformer(ContentCreationUtil contentCreationUtil) {
    this.contentCreationUtil = contentCreationUtil;
  }

  @NonNull
  @Override
  public ContentModel transform(Item item, ContentHubAdapter contentHubAdapter, ContentHubContext contentHubContext) {
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
    List<ContentModelReference> refs = new ArrayList<>();
    ThumbnailDetails thumbnails = video.getVideo().getSnippet().getThumbnails();
    if (thumbnails != null) {
      String url = null;
      if (thumbnails.getMaxres() != null) {
        url = thumbnails.getMaxres().getUrl();
      }

      if (url == null && thumbnails.getHigh() != null) {
        url = thumbnails.getHigh().getUrl();
      }

      if (url == null && thumbnails.getDefault() != null) {
        url = thumbnails.getDefault().getUrl();
      }

      if (url != null) {
        ContentModelReference contentModelRef = ContentModelReference.create(contentModel, "CMPicture", url);
        refs.add(contentModelRef);
      }
    }

    contentModel.put("pictures", refs);

    return contentModel;
  }

  @Override
  @Nullable
  public ContentModel resolveReference(ContentModelReference reference, ContentHubAdapter contentHubAdapter, ContentHubContext contentHubContext) {
    String imageUrl = (String) reference.getData();
    String imageName = contentCreationUtil.extractNameFromUrl(imageUrl);
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

}
