package com.coremedia.blueprint.contenthub.adapters.youtube.connector.search;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class YouTubeSearchConfiguration {
  @Bean
  public YouTubeSearchServiceProvider youTubeSearchServiceProvider() {
    return new YouTubeSearchServiceProvider();
  }
}
