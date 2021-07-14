package com.coremedia.blueprint.taxonomies;

import com.coremedia.blueprint.taxonomies.cycleprevention.TaxonomyCyclePreventionConfiguration;
import com.coremedia.blueprint.taxonomies.cycleprevention.TaxonomyCycleValidator;
import com.coremedia.blueprint.taxonomies.semantic.SemanticTaxonomyConfiguration;
import com.coremedia.blueprint.taxonomies.strategy.TaxonomyResolverImpl;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.spring.ContentConfigurationProperties;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.undoc.common.spring.CapRepositoriesConfiguration;
import com.coremedia.rest.cap.CapRestServiceSearchConfiguration;
import com.coremedia.rest.cap.content.search.SearchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;

@Configuration(proxyBeanMethods = false)
@Import({
        TaxonomyCyclePreventionConfiguration.class,
        SemanticTaxonomyConfiguration.class,
        CapRestServiceSearchConfiguration.class,
        CapRepositoriesConfiguration.class,
})
@EnableConfigurationProperties(ContentConfigurationProperties.class)
public class TaxonomyConfiguration {

  @Value("${taxonomies.maxDocumentsPerFolder:1000}")
  private int maxDocumentsPerFolder;

  /*
   * The CM taxonomy node strategy beans working on folders with parent and child properties on the CMTaxonomy content type.
   */
  @Bean
  TaxonomyResolverImpl strategyResolver(ContentRepository contentRepository,
                                        SitesService sitesService,
                                        SearchService searchService,
                                        TaxonomyCycleValidator taxonomyCycleValidator,
                                        ContentConfigurationProperties contentConfigurationProperties,
                                        Cache cache) {
    String globalConfigurationPath = contentConfigurationProperties.getGlobalConfigurationPath();
    return new TaxonomyResolverImpl(sitesService,
            contentRepository,
            searchService,
            taxonomyCycleValidator,
            Map.of("Query", "Subject", "QueryLocation", "Location"),
            "CMTaxonomy",
            "Options/",
            globalConfigurationPath,
            maxDocumentsPerFolder,
            cache);
  }

}
