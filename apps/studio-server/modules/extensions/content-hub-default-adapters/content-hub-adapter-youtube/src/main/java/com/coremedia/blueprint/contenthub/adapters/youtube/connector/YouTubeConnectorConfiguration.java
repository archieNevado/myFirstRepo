package com.coremedia.blueprint.contenthub.adapters.youtube.connector;

import com.coremedia.blueprint.contenthub.adapters.youtube.connector.playlists.PlayListServiceConfiguration;
import com.coremedia.blueprint.contenthub.adapters.youtube.connector.playlists.PlayListServiceProvider;
import com.coremedia.blueprint.contenthub.adapters.youtube.connector.search.YouTubeSearchConfiguration;
import com.coremedia.blueprint.contenthub.adapters.youtube.connector.search.YouTubeSearchServiceProvider;
import com.coremedia.blueprint.contenthub.adapters.youtube.connector.videos.VideoServiceConfiguration;
import com.coremedia.blueprint.contenthub.adapters.youtube.connector.videos.VideoServiceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({YouTubeSearchConfiguration.class,
         PlayListServiceConfiguration.class,
         VideoServiceConfiguration.class})
public class YouTubeConnectorConfiguration {
  @Bean
  public YouTubeConnectorFactory youTubeConnectorFactory(YouTubeSearchServiceProvider youTubeSearchServiceProvider,
                                                         PlayListServiceProvider playListServiceProvider,
                                                         VideoServiceProvider videoServiceProvider) {
    return new YouTubeConnectorFactoryImpl(youTubeSearchServiceProvider, playListServiceProvider, videoServiceProvider);
  }
}
