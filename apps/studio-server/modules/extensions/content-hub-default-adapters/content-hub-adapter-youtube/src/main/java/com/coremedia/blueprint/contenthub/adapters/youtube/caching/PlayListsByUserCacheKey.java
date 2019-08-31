package com.coremedia.blueprint.contenthub.adapters.youtube.caching;

import com.coremedia.blueprint.contenthub.adapters.youtube.YouTubeContentHubAdapter;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
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
public class PlayListsByUserCacheKey extends CacheKey<List<Playlist>> {
  private static final String REQUEST_TYPE_BY_USER = "snippet,contentDetails";
  private static final String REQUEST_TYPE_SNIPPET = "snippet";

  private final YouTube youTube;
  private final String user;
  private int pollingIntervalMinutes;

  public PlayListsByUserCacheKey(YouTube youTube, String user, int pollingIntervalMinutes) {
    this.pollingIntervalMinutes = pollingIntervalMinutes;
    this.youTube = youTube;
    this.user = user;
  }

  @Override
  public List<Playlist> evaluate(Cache cache) throws IOException {
    Cache.cacheFor(pollingIntervalMinutes, TimeUnit.MINUTES);
    List<Playlist> playlists = new ArrayList<>();
    List<Channel> channels = new ArrayList<>();
    fetchChannelList(channels, user, null);
    for (Channel item : channels) {
      String channelItemId = item.getId();
      fetchPlayListResponse(playlists, channelItemId, null);
    }
    return playlists;
  }

  private void fetchPlayListResponse(List<Playlist> playlists, String channeItemId, String token) throws IOException {
    YouTube.Playlists.List list = youTube.playlists()
            .list(REQUEST_TYPE_SNIPPET)
            .setMaxResults(YouTubeContentHubAdapter.MAX_RESULTS)
            .setChannelId(channeItemId);

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
      fetchPlayListResponse(playlists, channeItemId, nextPageToken);
    }
  }


  private void fetchChannelList(List<Channel> channels, String user, String token) throws IOException {
    YouTube.Channels.List list = youTube.channels()
            .list(REQUEST_TYPE_BY_USER)
            .setMaxResults(YouTubeContentHubAdapter.MAX_RESULTS)
            .setForUsername(user);

    if(token != null) {
      list.setPageToken(token);
    }

    ChannelListResponse response = list.execute();
    List<Channel> items = response.getItems();
    if(items != null) {
      channels.addAll(items);
    }

    String nextPageToken = response.getNextPageToken();
    if(nextPageToken != null) {
      fetchChannelList(channels, user, nextPageToken);
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
    PlayListsByUserCacheKey that = (PlayListsByUserCacheKey) o;
    return that.user.equals(this.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user);
  }
}
