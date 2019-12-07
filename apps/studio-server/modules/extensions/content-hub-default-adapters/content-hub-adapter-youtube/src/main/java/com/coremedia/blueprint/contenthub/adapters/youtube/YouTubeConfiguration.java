package com.coremedia.blueprint.contenthub.adapters.youtube;

import com.coremedia.contenthub.BundleConfiguration;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        BundleConfiguration.class,
})
public class YouTubeConfiguration {
  @Bean
  public ContentHubAdapterFactory youTubeContentHubAdapterFactory() {
    return new YouTubeContentHubAdapterFactory();
  }
}
