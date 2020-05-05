package com.coremedia.blueprint.contenthub.adapters.youtube.connector;

import com.coremedia.blueprint.contenthub.adapters.youtube.YouTubeContentHubSettings;
import edu.umd.cs.findbugs.annotations.NonNull;

public interface YouTubeConnectorFactory {

  @NonNull
  YouTubeConnectorImpl create(@NonNull YouTubeContentHubSettings settings, @NonNull String connectionId);
}
