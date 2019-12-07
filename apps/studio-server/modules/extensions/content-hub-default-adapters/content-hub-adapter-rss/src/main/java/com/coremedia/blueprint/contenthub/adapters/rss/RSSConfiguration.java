package com.coremedia.blueprint.contenthub.adapters.rss;

import com.coremedia.contenthub.BundleConfiguration;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
      BundleConfiguration.class,
})
public class RSSConfiguration {
  @Bean
  public ContentHubAdapterFactory rssContentHubAdapterFactory() {
    return new RSSContentHubAdapterFactory();
  }
}
