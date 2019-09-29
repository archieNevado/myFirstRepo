package com.coremedia.blueprint.contenthub.adapters.rss;

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
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RSSContentHubTransformer implements ContentHubTransformer {

  private final ContentCreationUtil contentCreationUtil;

  RSSContentHubTransformer(@NonNull ContentCreationUtil contentCreationUtil) {
    this.contentCreationUtil = contentCreationUtil;
  }

  @NonNull
  @Override
  public ContentModel transform(Item item, ContentHubAdapter contentHubAdapter, ContentHubContext contentHubContext) {
    RSSItem rssItem = (RSSItem) item;
    ContentModel contentModel = new ContentModel(rssItem.getRssEntry().getTitle(), item.getId());
    contentModel.put("title", rssItem.getName());
    String description = extractDescription(rssItem);
    if (description != null) {
      contentModel.put("detailText", contentCreationUtil.convertStringToMarkup(description));
    }

    SyndEntry rssEntry = rssItem.getRssEntry();
    List<String> imageUrls = FeedImageExtractor.extractImageUrls(rssEntry);
    List<ContentModelReference> refs = new ArrayList<>();
    for (String imageUrl : imageUrls) {
      ContentModelReference contentModelRef = ContentModelReference.create(contentModel, "CMPicture", imageUrl);
      refs.add(contentModelRef);
    }
    contentModel.put("pictures", refs);

    return contentModel;
  }

  @Override
  @Nullable
  public ContentModel resolveReference(ContentModelReference reference, ContentHubAdapter contentHubAdapter, ContentHubContext contentHubContext) {
    String imageUrl = (String) reference.getData();
    String imageName = contentCreationUtil.extractNameFromUrl(imageUrl);
    if (imageName == null) {
      return null;
    }
    ContentHubObjectId contentHubObjectId = reference.getOwner().getContentHubObjectId();
    ContentHubObjectId referenceId = ContentHubObjectId.createReference(contentHubObjectId, imageName);
    ContentModel contentModel = new ContentModel(imageName, referenceId);
    Blob pictureBlob = contentCreationUtil.createPictureFromUrl(imageUrl,
            "Image " + imageName,
            MimeTypeFactory.create("image/jpeg"));
    contentModel.put("data", pictureBlob);
    contentModel.put("title", "Image " + imageName);

    return contentModel;
  }

  @Override
  public boolean isApplicable(ContentHubObject contentHubObject) {
    return contentHubObject instanceof RSSHubObject;
  }


  // --- internal ---------------------------------------------------

  @Nullable
  private String extractDescription(@Nullable RSSItem rssItem) {
    SyndEntry rssEntry = rssItem==null ? null : rssItem.getRssEntry();
    SyndContent syndContent = rssEntry==null ? null : rssEntry.getDescription();
    return syndContent==null ? null : syndContent.getValue();
  }
}
