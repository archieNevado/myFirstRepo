package com.coremedia.blueprint.contenthub.adapters.youtube.caching;

import com.coremedia.blueprint.contenthub.adapters.youtube.YouTubeContentHubAdapter;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class PlayListItemsCacheKey extends CacheKey<List<PlaylistItem>> {
  private static final String REQUEST_TYPE_SNIPPET = "snippet";

  private final YouTube youTube;
  private final String playlistId;
  private int pollingIntervalMinutes;

  public PlayListItemsCacheKey(YouTube youTube, String playlistId, int pollingIntervalMinutes) {
    this.youTube = youTube;
    this.playlistId = playlistId;
    this.pollingIntervalMinutes = pollingIntervalMinutes;
  }

  @Override
  public List<PlaylistItem> evaluate(Cache cache) throws IOException {
    Cache.cacheFor(pollingIntervalMinutes, TimeUnit.MINUTES);
    List<PlaylistItem> result = new ArrayList<>();
    fetchPlayListItems(result, null);
    return result;
  }

  private void fetchPlayListItems(List<PlaylistItem> items, @Nullable String token) throws IOException {

    YouTube.PlaylistItems.List list = youTube.playlistItems()
            .list(REQUEST_TYPE_SNIPPET)
            .setMaxResults(YouTubeContentHubAdapter.MAX_RESULTS)
            .setPlaylistId(playlistId);

    if (token != null) {
      list.setPageToken(token);
    }

    PlaylistItemListResponse response = list.execute();
    List<PlaylistItem> responseItems= response.getItems();
    if (responseItems != null) {
      items.addAll(responseItems);
    }

    String nextPageToken = response.getNextPageToken();
    if(nextPageToken != null) {
      fetchPlayListItems(items, nextPageToken);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PlayListItemsCacheKey that = (PlayListItemsCacheKey) o;
    return that.playlistId.equals(this.playlistId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(playlistId);
  }
}
