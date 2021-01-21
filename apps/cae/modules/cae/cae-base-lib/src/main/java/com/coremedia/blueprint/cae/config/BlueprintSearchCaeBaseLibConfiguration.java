package com.coremedia.blueprint.cae.config;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.action.search.SearchService;
import com.coremedia.blueprint.cae.action.search.TaxonomyFacetFieldLabelFunction;
import com.coremedia.blueprint.cae.search.SearchResultFactory;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.id.IdProvider;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.springframework.customizer.Customize;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Search specific bean definitions.
 */
@Configuration(proxyBeanMethods = false)
@ImportResource(value = {
        "classpath:/framework/spring/search/solr-search.xml",
        "classpath:/com/coremedia/blueprint/base/settings/impl/bpbase-settings-services.xml",
        "classpath:/com/coremedia/cae/contentbean-services.xml",
        "classpath:/framework/spring/blueprint-services.xml"
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
public class BlueprintSearchCaeBaseLibConfiguration {

  /**
   * The search service.
   */
  @Bean
  public SearchService searchActionService(SearchResultFactory resultFactory,
                                           ContentBeanFactory contentBeanFactory,
                                           ContentRepository contentRepository,
                                           SettingsService settingsService) {
    SearchService searchService = new SearchService();

    searchService.setHighlightingEnabled(true);
    searchService.setResultFactory(resultFactory);
    searchService.setContentBeanFactory(contentBeanFactory);
    searchService.setContentRepository(contentRepository);
    searchService.setSettingsService(settingsService);

    return searchService;
  }

  @Bean(autowireCandidate = false)
  @Customize(value = "facetFieldLabelFunctions", mode = Customize.Mode.APPEND)
  @Order(10000)
  public Map<String, Function> addTaxonomyFacetFieldLabelFunction(TaxonomyFacetFieldLabelFunction taxonomyFacetFieldLabelFunction) {
    Map<String, Function> map = new HashMap<>(2);

    map.put("subjecttaxonomy", taxonomyFacetFieldLabelFunction);
    map.put("locationtaxonomy", taxonomyFacetFieldLabelFunction);

    return map;
  }

  @Bean
  public TaxonomyFacetFieldLabelFunction taxonomyFacetFieldLabelFunction(IdProvider idProvider,
                                                                         ValidationService validationService) {
    return new TaxonomyFacetFieldLabelFunction(idProvider, validationService);
  }
}
