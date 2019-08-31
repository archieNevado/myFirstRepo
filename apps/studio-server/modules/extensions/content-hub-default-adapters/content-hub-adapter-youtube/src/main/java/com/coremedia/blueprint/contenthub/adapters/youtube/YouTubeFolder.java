package com.coremedia.blueprint.contenthub.adapters.youtube;


import com.coremedia.contenthub.api.ContentHubAdapterBinding;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.Folder;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistSnippet;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Date;

public class YouTubeFolder extends YouTubeHubObject implements Folder {

  private String name;
  private Playlist playlist;

  YouTubeFolder(ContentHubAdapterBinding binding, ContentHubObjectId id, String name) {
    super(binding, id);
    this.name = name;
  }

  YouTubeFolder(ContentHubAdapterBinding binding, ContentHubObjectId id, Playlist playlist) {
    super(binding, id);
    this.playlist = playlist;
    this.name = playlist.getSnippet().getTitle();
  }

  @NonNull
  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getType() {
    if(getId().isRootId()) {
      return "youtubechannel";
    }
    return "playlist";
  }

  @Override
  public Date getLastModified() {
    if(playlist != null) {
      PlaylistSnippet snippet = playlist.getSnippet();
      return new Date(snippet.getPublishedAt().getValue() + snippet.getPublishedAt().getTimeZoneShift() * 60000L);
    }
    return null;
  }
}
