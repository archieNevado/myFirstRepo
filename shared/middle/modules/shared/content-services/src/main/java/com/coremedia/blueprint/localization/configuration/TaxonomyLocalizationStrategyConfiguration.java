package com.coremedia.blueprint.localization.configuration;

import com.coremedia.blueprint.base.multisite.BlueprintMultisiteConfiguration;
import com.coremedia.blueprint.base.taxonomies.TaxonomyLocalizationStrategy;
import com.coremedia.blueprint.localization.TaxonomyLocalizationStrategyImpl;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.SitesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({
        BlueprintMultisiteConfiguration.class,
})
public class TaxonomyLocalizationStrategyConfiguration {

  @Value("${taxonomy.localization.struct:localSettings}")
  private String structProperty = "localSettings";

  @Bean
  TaxonomyLocalizationStrategy taxonomyLocalizationStrategy(ContentRepository contentRepository, SitesService sitesService, Cache cache) {
    return new TaxonomyLocalizationStrategyImpl(contentRepository, sitesService, cache, structProperty);
  }
}
