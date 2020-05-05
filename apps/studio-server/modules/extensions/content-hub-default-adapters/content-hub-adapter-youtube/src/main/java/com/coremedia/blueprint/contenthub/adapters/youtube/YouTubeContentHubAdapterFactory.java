package com.coremedia.blueprint.contenthub.adapters.youtube;

import com.coremedia.blueprint.contenthub.adapters.youtube.connector.YouTubeConnector;
import com.coremedia.blueprint.contenthub.adapters.youtube.connector.YouTubeConnectorFactory;
import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import edu.umd.cs.findbugs.annotations.NonNull;

class YouTubeContentHubAdapterFactory implements ContentHubAdapterFactory<YouTubeContentHubSettings> {

  private YouTubeConnectorFactory youTubeConnectorFactory;

  YouTubeContentHubAdapterFactory(YouTubeConnectorFactory youTubeConnectorFactory) {
    this.youTubeConnectorFactory = youTubeConnectorFactory;
  }

  @Override
  @NonNull
  public String getId() {
    return "youtube";
  }

  @Override
  @NonNull
  public ContentHubAdapter createAdapter(@NonNull YouTubeContentHubSettings settings,
                                         @NonNull String connectionID){
    YouTubeConnector youTubeConnector = youTubeConnectorFactory.create(settings, connectionID);
    return new YouTubeContentHubAdapter(youTubeConnector, settings, connectionID);
  }
}
