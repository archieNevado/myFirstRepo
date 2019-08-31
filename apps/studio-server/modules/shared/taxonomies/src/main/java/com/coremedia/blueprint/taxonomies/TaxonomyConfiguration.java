package com.coremedia.blueprint.taxonomies;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.taxonomies.semantic.SemanticEntity;
import com.coremedia.blueprint.taxonomies.semantic.SemanticStrategy;
import com.coremedia.blueprint.taxonomies.semantic.service.SemanticServiceStrategy;
import com.coremedia.blueprint.taxonomies.semantic.service.matching.NameMatchingStrategy;
import com.coremedia.blueprint.taxonomies.semantic.service.opencalais.CalaisService;
import com.coremedia.blueprint.taxonomies.strategy.TaxonomyResolverImpl;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.rest.cap.config.StudioConfigurationProperties;
import com.coremedia.rest.cap.CapRestServiceConfiguration;
import com.coremedia.rest.cap.content.search.solr.SolrSearchService;
import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Import(CapRestServiceConfiguration.class)
@ImportResource(
        value = {
                "classpath:com/coremedia/cap/common/uapi-services.xml",
                "classpath:com/coremedia/blueprint/base/settings/impl/bpbase-settings-services.xml"
        },
        reader = ResourceAwareXmlBeanDefinitionReader.class
)
public class TaxonomyConfiguration {

  @Bean
  CalaisService calaisService(@Value("${semantic.service.opencalais.api.key:enter-api-key-here}") String apiKey,
                              @Value("${semantic.service.opencalais.api.url:https://api.thomsonreuters.com/permid/calais}") String apiUrl,
                              Cache cache,
                              SettingsService settingsService,
                              SitesService sitesService) {
    CalaisService service = new CalaisService();
    service.setApiKey(apiKey);
    service.setApiUrl(apiUrl);
    service.setCache(cache);
    service.setSettingsService(settingsService);
    service.setSitesService(sitesService);
    service.setGroupingKey("_uri");

    List<String> documentProperties = Arrays.asList("title", "teaserTitle", "detailText", "teaserText");
    service.setDocumentProperties(documentProperties);

    Map<String,String> semProps = new HashMap<>(5);
    semProps.put(SemanticEntity.ID, "_uri");
    semProps.put(SemanticEntity.NAME, "name");
    semProps.put(SemanticEntity.TYPE, "_type");
    semProps.put("typeRef", "_typeReference");
    semProps.put("relevance", "relevance");
    service.setSemanticProperties(semProps);

    return service;
  }

  @Bean
  SemanticServiceStrategy semanticService(ContentRepository contentRepository,
                                          CalaisService calaisService,
                                          SolrSearchService solrSearchService) {
    SemanticServiceStrategy strategy = new SemanticServiceStrategy();
    strategy.setContentRepository(contentRepository);
    strategy.setSemanticService(calaisService);
    strategy.setSolrSearchService(solrSearchService);
    strategy.setNameMatchingPropertyName("value");
    strategy.setReferencePropertyName("externalReference");
    strategy.setServiceId("semantic");

    return strategy;
  }

  /*
   * The CM taxonomy node strategy beans working on folders with parent and child properties on the CMTaxonomy content type.
   */
  @Bean
  @Scope("prototype")
  TaxonomyResolverImpl strategyResolver(ContentRepository contentRepository,
                                        SitesService sitesService,
                                        SolrSearchService solrSearchService,
                                        StudioConfigurationProperties studioConfigurationProperties) {
    TaxonomyResolverImpl resolver = new TaxonomyResolverImpl();
    resolver.setContentRepository(contentRepository);
    resolver.setSitesService(sitesService);
    resolver.setSolrSearchService(solrSearchService);
    resolver.setContentType("CMTaxonomy");
    resolver.setSiteConfigPath("Options/");
    resolver.setGlobalConfigPath(studioConfigurationProperties.getGlobalConfigurationPath());

    Map<String, String> mapping = new HashMap<>(2);
    mapping.put("Query", "Subject");
    mapping.put("QueryLocation", "Location");
    resolver.setAliasMapping(mapping);

    return resolver;
  }

  @Bean
  NameMatchingStrategy nameMatching(ContentRepository contentRepository) {
    NameMatchingStrategy strategy = new NameMatchingStrategy();
    strategy.setContentRepository(contentRepository);
    strategy.setServiceId("nameMatching");

    return strategy;
  }

  @Bean
  List<SemanticStrategy> semanticServiceStrategies() {
    return new ArrayList<>();
  }

  @Bean
  @Order(1000)
  @Customize("semanticServiceStrategies")
  List<String> semanticStrategyExamplesCustomizer() {
    return Arrays.asList("nameMatching", "semanticService");
  }
}
