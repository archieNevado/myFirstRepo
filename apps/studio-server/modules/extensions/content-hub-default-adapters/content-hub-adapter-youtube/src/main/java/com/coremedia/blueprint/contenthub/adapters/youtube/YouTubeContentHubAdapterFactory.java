package com.coremedia.blueprint.contenthub.adapters.youtube;

import com.coremedia.cache.Cache;
import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubAdapterBinding;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import edu.umd.cs.findbugs.annotations.NonNull;
public class YouTubeContentHubAdapterFactory implements ContentHubAdapterFactory<YouTubeContentHubSettings> {

  private Cache cache;

  public YouTubeContentHubAdapterFactory(Cache cache) {
    this.cache = cache;
  }

  @Override
  @NonNull
  public String getId() {
    return "youtube";
  }

  @Override
  @NonNull
  public ContentHubAdapter createAdapter(@NonNull ContentHubAdapterBinding<YouTubeContentHubSettings> binding) {
    return new YouTubeContentHubAdapter(binding, cache);
  }
}
