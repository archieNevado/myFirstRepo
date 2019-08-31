package com.coremedia.blueprint.contenthub.adapters.youtube.caching;

import com.coremedia.blueprint.contenthub.adapters.youtube.YouTubeContentHubAdapter;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class VideoCacheKey extends CacheKey<Video> {
  private static final String REQUEST_TYPE_SNIPPET = "snippet";

  private final YouTube youTube;
  private final String videoId;
  private int pollingIntervalMinutes;

  public VideoCacheKey(YouTube youTube, String videoId, int pollingIntervalMinutes) {
    this.pollingIntervalMinutes = pollingIntervalMinutes;
    this.youTube = youTube;
    this.videoId = videoId;
  }

  @Override
  public Video evaluate(Cache cache) throws IOException {
    Cache.cacheFor(pollingIntervalMinutes, TimeUnit.MINUTES);
    VideoListResponse videoListResponse = youTube.videos()
            .list(REQUEST_TYPE_SNIPPET)
            .setMaxResults(YouTubeContentHubAdapter.MAX_RESULTS)
            .setId(videoId)
            .execute();

    List<Video> videos = videoListResponse.getItems();
    if (videos != null && !videos.isEmpty()) {
      return videos.get(0);
    }

    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VideoCacheKey that = (VideoCacheKey) o;
    return that.videoId.equals(this.videoId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(videoId);
  }
}
