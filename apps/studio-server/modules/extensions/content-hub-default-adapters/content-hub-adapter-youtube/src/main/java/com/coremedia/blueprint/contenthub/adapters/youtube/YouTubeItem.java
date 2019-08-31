package com.coremedia.blueprint.contenthub.adapters.youtube;


import com.coremedia.contenthub.api.ContentHubAdapterBinding;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.Item;
import com.coremedia.contenthub.api.MimeTypeFactory;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import javax.activation.MimeType;
import java.util.Date;

public class YouTubeItem extends YouTubeHubObject implements Item {

  private Video video;

  YouTubeItem(ContentHubAdapterBinding binding, ContentHubObjectId id, Video video) {
    super(binding, id);
    this.video = video;
  }

  Video getVideo() {
    return video;
  }

  @Nullable
  @Override
  public MimeType getItemType() {
    return MimeTypeFactory.create("youtube");
  }

  @Nullable
  @Override
  public String getTargetContentType() {
    return "CMVideo";
  }

  @Override
  public long getSize() {
    return -1;
  }

  @NonNull
  @Override
  public String getName() {

    return video.getSnippet().getTitle();
  }

  @Nullable
  @Override
  public String getDescription() {
    return video.getSnippet().getDescription();
  }

  @Override
  public Date getLastModified() {
    VideoSnippet snippet = video.getSnippet();
    return new Date(snippet.getPublishedAt().getValue() + snippet.getPublishedAt().getTimeZoneShift() * 60000L);
  }
}
