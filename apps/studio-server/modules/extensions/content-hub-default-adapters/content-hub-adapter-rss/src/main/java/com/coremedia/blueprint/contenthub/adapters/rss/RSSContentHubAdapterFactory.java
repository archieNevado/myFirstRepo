package com.coremedia.blueprint.contenthub.adapters.rss;

import com.coremedia.blueprint.contenthub.adapters.rss.imageurlextractor.FeedImageExtractor;
import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import edu.umd.cs.findbugs.annotations.NonNull;

/**
 *
 */
class RSSContentHubAdapterFactory implements ContentHubAdapterFactory<RSSContentHubSettings> {

  private final RSSContentHubTransformer rssContentHubTransformer;
  private final FeedImageExtractor feedImageExtractor;

  RSSContentHubAdapterFactory(@NonNull RSSContentHubTransformer rssContentHubTransformer,
                              @NonNull FeedImageExtractor feedImageExtractor) {
    this.rssContentHubTransformer = rssContentHubTransformer;
    this.feedImageExtractor = feedImageExtractor;
  }

  @Override
  @NonNull
  public String getId() {
    return "rss";
  }

  @NonNull
  @Override
  public ContentHubAdapter createAdapter(@NonNull RSSContentHubSettings settings,
                                         @NonNull String connectionId) {
    return new RSSContentHubAdapter(rssContentHubTransformer, feedImageExtractor, settings, connectionId);
  }

}
