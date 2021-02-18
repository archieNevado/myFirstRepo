package com.coremedia.blueprint.contenthub.adapters.youtube.connector.playlists;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlayListServiceConfiguration {
  @Bean
  PlayListServiceProvider playListServiceProvider() {
    return new PlayListServiceProvider();
  }
}
