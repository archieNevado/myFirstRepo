package com.coremedia.blueprint.contenthub.adapters.rss;

import com.coremedia.contenthub.BundleConfiguration;
import com.coremedia.contenthub.api.ContentCreationUtil;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
      BundleConfiguration.class,
})
public class RSSConfiguration {

  @Bean
  public RSSColumnModelProvider rssContentHubColumnModelProvider() {
    return new RSSColumnModelProvider();
  }

  @Bean
  public RSSContentHubAdapterFactory rssContentHubAdapterFactory() {
    return new RSSContentHubAdapterFactory();
  }

  @Bean
  public RSSContentHubTransformer rssContentHubTransformer(@NonNull ContentCreationUtil contentCreationUtil) {
    return new RSSContentHubTransformer(contentCreationUtil);
  }
}
