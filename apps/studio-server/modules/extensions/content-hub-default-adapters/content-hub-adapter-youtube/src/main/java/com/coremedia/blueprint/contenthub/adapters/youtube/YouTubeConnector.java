package com.coremedia.blueprint.contenthub.adapters.youtube;

import com.coremedia.blueprint.contenthub.adapters.youtube.caching.ChannelSearchResultCacheKey;
import com.coremedia.blueprint.contenthub.adapters.youtube.caching.PlayListItemsCacheKey;
import com.coremedia.blueprint.contenthub.adapters.youtube.caching.PlayListsByChannelCacheKey;
import com.coremedia.blueprint.contenthub.adapters.youtube.caching.PlayListsByUserCacheKey;
import com.coremedia.blueprint.contenthub.adapters.youtube.caching.VideoCacheKey;
import com.coremedia.cache.Cache;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * The YouTubeConnector is responsible for the connection to youtube.
 * The requests to youtube's API are cached, and the cache is invalidated every 60 minutes.
 */
class YouTubeConnector {

  private static final int POLLING_INTERVAL_MINUTES = 60;
  private YouTube youTube;
  private Cache cache;

  public YouTubeConnector(YouTube youTube, Cache cache) {
    this.youTube = youTube;
    this.cache = cache;
  }

  List<Playlist> getPlaylistsByUser(String user) {
    return cache.get(new PlayListsByUserCacheKey(youTube, user, POLLING_INTERVAL_MINUTES));
  }

  List<Playlist> getPlaylistsByChannel(String channelId) {
    return cache.get(new PlayListsByChannelCacheKey(youTube, channelId, POLLING_INTERVAL_MINUTES));
  }

  Video getVideo(String videoId) {
    return cache.get(new VideoCacheKey(youTube, videoId, POLLING_INTERVAL_MINUTES));
  }

  List<Video> getVideos(String channelId) {
    List<Video> result = new ArrayList<>();
    List<String> videoIds = cache.get(new ChannelSearchResultCacheKey(youTube, channelId, " ", POLLING_INTERVAL_MINUTES));
    for (String videoId : videoIds) {
      Video video = getVideo(videoId);
      if (video != null) {
        result.add(video);
      }
    }
    return result;
  }

  public List<PlaylistItem> getPlaylistItems(String playlistId) {
    return cache.get(new PlayListItemsCacheKey(youTube, playlistId, POLLING_INTERVAL_MINUTES));
  }
}
