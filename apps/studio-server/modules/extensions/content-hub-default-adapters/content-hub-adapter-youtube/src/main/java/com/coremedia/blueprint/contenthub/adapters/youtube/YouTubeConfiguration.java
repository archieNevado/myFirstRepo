package com.coremedia.blueprint.contenthub.adapters.youtube;

import com.coremedia.blueprint.contenthub.adapters.youtube.connector.YouTubeConnectorConfiguration;
import com.coremedia.blueprint.contenthub.adapters.youtube.connector.YouTubeConnectorFactory;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(YouTubeConnectorConfiguration.class)
public class YouTubeConfiguration {
  @Bean
  public ContentHubAdapterFactory youTubeContentHubAdapterFactory(YouTubeConnectorFactory youTubeConnectorFactory) {
    return new YouTubeContentHubAdapterFactory(youTubeConnectorFactory);
  }
}
