package com.coremedia.blueprint.contenthub.adapters.youtube;

import com.coremedia.cache.Cache;
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
public class YouTubeConfiguration {

  @Bean
  public YouTubeColumnModelProvider youTubeContentHubColumnModelProvider() {
    return new YouTubeColumnModelProvider();
  }

  @Bean
  public YouTubeContentHubAdapterFactory youTubeContentHubAdapterFactory(@NonNull Cache cache) {
    return new YouTubeContentHubAdapterFactory(cache);
  }

  @Bean
  public YouTubeContentHubTransformer youtTubeContentHubTransformer(@NonNull ContentCreationUtil contentCreationUtil) {
    return new YouTubeContentHubTransformer(contentCreationUtil);
  }
}
