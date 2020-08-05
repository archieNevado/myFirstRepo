package com.coremedia.blueprint.contenthub.adapters.rss.imageurlextractor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedImageExtractorConfiguration {

  @Bean
  FeedImageExtractor feedImageExtractor() {
    return new FeedImageExtractorImpl();
  }
}
