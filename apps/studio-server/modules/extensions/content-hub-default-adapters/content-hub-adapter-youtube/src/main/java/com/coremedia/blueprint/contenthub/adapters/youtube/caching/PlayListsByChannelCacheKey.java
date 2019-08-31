package com.coremedia.blueprint.contenthub.adapters.youtube.caching;

import com.coremedia.blueprint.contenthub.adapters.youtube.YouTubeContentHubAdapter;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class PlayListsByChannelCacheKey extends CacheKey<List<Playlist>> {
  private static final String REQUEST_TYPE_SNIPPET = "snippet";

  private final YouTube youTube;
  private final String channelId;
  private int pollingIntervalMinutes;

  public PlayListsByChannelCacheKey(YouTube youTube, String channelId, int pollingIntervalMinutes) {
    this.youTube = youTube;
    this.channelId = channelId;
    this.pollingIntervalMinutes = pollingIntervalMinutes;
  }

  @Override
  public List<Playlist> evaluate(Cache cache) throws IOException {
    Cache.cacheFor(pollingIntervalMinutes, TimeUnit.MINUTES);
    List<Playlist> playlists = new ArrayList<>();
    fetchPlayListResponse(playlists, null);
    return playlists;
  }

  private void fetchPlayListResponse(List<Playlist> playlists, String token) throws IOException {
    YouTube.Playlists.List list = youTube.playlists()
            .list(REQUEST_TYPE_SNIPPET)
            .setMaxResults(YouTubeContentHubAdapter.MAX_RESULTS)
            .setChannelId(channelId);

    if(token != null) {
      list.setPageToken(token);
    }

    PlaylistListResponse response = list.execute();
    List<Playlist> items = response.getItems();
    if(items != null) {
      playlists.addAll(items);
    }

    String nextPageToken = response.getNextPageToken();
    if(nextPageToken != null) {
      fetchPlayListResponse(playlists, nextPageToken);
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
    PlayListsByChannelCacheKey that = (PlayListsByChannelCacheKey) o;
    return that.channelId.equals(this.channelId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channelId);
  }
}
