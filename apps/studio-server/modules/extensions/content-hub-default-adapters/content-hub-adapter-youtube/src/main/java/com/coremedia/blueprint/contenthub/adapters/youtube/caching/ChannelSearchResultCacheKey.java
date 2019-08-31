package com.coremedia.blueprint.contenthub.adapters.youtube.caching;

import com.coremedia.blueprint.contenthub.adapters.youtube.YouTubeContentHubAdapter;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class ChannelSearchResultCacheKey extends CacheKey<List<String>> {
  private static final String YOUTUBE_VIDEO_TYPE_SNIPPET = "video";
  private static final String REQUEST_TYPE_SNIPPET = "snippet";

  private final YouTube youTube;
  private final String channelId;
  private String term;
  private int pollingIntervalMinutes;

  public ChannelSearchResultCacheKey(YouTube youTube, String channelId, String term, int pollingIntervalMinutes) {
    this.youTube = youTube;
    this.channelId = channelId;
    this.term = term;
    this.pollingIntervalMinutes = pollingIntervalMinutes;
  }

  @Override
  public List<String> evaluate(Cache cache) throws IOException {
    Cache.cacheFor(pollingIntervalMinutes, TimeUnit.MINUTES);
    List<String> result = new ArrayList<>();
    fetchSearchListResponse(result, channelId, term, null);
    return result;
  }

  private void fetchSearchListResponse(List<String> searchResults, String channelId, String term, String token) throws IOException {
    YouTube.Search.List list = youTube.search()
            .list(REQUEST_TYPE_SNIPPET)
            .setMaxResults(YouTubeContentHubAdapter.MAX_RESULTS)
            .setChannelId(channelId)
            .setType(YOUTUBE_VIDEO_TYPE_SNIPPET)
            .setQ(term);

    if(token != null) {
      list.setPageToken(token);
    }

    SearchListResponse response = list.execute();
    List<SearchResult> items = response.getItems();
    if(items != null) {
      for (SearchResult searchResult : items) {
        String videoId = searchResult.getId().getVideoId();
        searchResults.add(videoId);
      }
    }

    String nextPageToken = response.getNextPageToken();
    if(nextPageToken != null) {
      fetchSearchListResponse(searchResults, channelId, term, nextPageToken);
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
    ChannelSearchResultCacheKey that = (ChannelSearchResultCacheKey) o;
    return that.term.equals(this.term) && that.channelId.equals(this.channelId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channelId + term);
  }
}
