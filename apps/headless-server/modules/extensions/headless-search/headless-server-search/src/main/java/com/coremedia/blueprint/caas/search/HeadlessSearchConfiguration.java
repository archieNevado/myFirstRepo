package com.coremedia.blueprint.caas.search;

import com.coremedia.blueprint.base.caas.model.adapter.NavigationAdapterFactory;
import com.coremedia.blueprint.base.caas.model.adapter.QueryListAdapterFactory;
import com.coremedia.blueprint.base.caas.model.adapter.SearchServiceAdapterFactory;
import com.coremedia.blueprint.base.caas.model.adapter.TaxonomyAdapterFactory;
import com.coremedia.blueprint.base.caas.model.adapter.TaxonomyHelper;
import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.caas.config.CaasSearchConfigurationProperties;
import com.coremedia.caas.model.adapter.ExtendedLinkListAdapterFactory;
import com.coremedia.caas.search.id.CaasContentBeanIdScheme;
import com.coremedia.caas.search.model.FilterQueryArg;
import com.coremedia.caas.search.schema.CoercingFilterQueryArg;
import com.coremedia.caas.search.solr.SearchConstants;
import com.coremedia.caas.search.solr.SearchQueryHelper;
import com.coremedia.caas.search.solr.SolrCaeQueryBuilder;
import com.coremedia.caas.search.solr.SolrQueryBuilder;
import com.coremedia.caas.search.solr.SolrSearchResultFactory;
import com.coremedia.caas.web.CaasServiceConfigurationProperties;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.id.IdScheme;
import com.coremedia.search.solr.client.SolrClientConfiguration;
import com.coremedia.springframework.xml.ResourceAwareXmlBeanDefinitionReader;
import graphql.GraphqlErrorException;
import graphql.schema.GraphQLScalarType;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        CaasSearchConfigurationProperties.class,
        CaasServiceConfigurationProperties.class,
})
@Import({
        SolrClientConfiguration.class,
})
@ImportResource(value = {
        "classpath:/com/coremedia/blueprint/base/settings/impl/bpbase-settings-services.xml",
        "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml",
        "classpath:/com/coremedia/blueprint/base/navigation/context/bpbase-default-contextstrategy.xml",
}, reader = ResourceAwareXmlBeanDefinitionReader.class)
public class HeadlessSearchConfiguration {

  private final CaasSearchConfigurationProperties caasSearchConfigurationProperties;
  private final CaasServiceConfigurationProperties caasServiceConfigurationProperties;

  public HeadlessSearchConfiguration(CaasSearchConfigurationProperties caasSearchConfigurationProperties,
                                     CaasServiceConfigurationProperties caasServiceConfigurationProperties) {
    this.caasSearchConfigurationProperties = caasSearchConfigurationProperties;
    this.caasServiceConfigurationProperties = caasServiceConfigurationProperties;
  }

  @Bean
  public SearchServiceAdapterFactory searchServiceAdapter(@Qualifier("searchResultFactory") SolrSearchResultFactory searchResultFactory,
                                                          ContentRepository contentRepository,
                                                          @Qualifier("settingsService") SettingsService settingsService,
                                                          SitesService sitesService,
                                                          List<IdScheme> idSchemes,
                                                          @Qualifier("caeSolrQueryBuilder") SolrQueryBuilder solrQueryBuilder,
                                                          @Qualifier("customStaticFilterQueries") List<List<FilterQueryArg>> customStaticFilterQueries) {
    List<FilterQueryArg> customStaticFilterQueriesList = customStaticFilterQueries.stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
    return new SearchServiceAdapterFactory(searchResultFactory, contentRepository, settingsService, sitesService, idSchemes, solrQueryBuilder, caasSearchConfigurationProperties, customStaticFilterQueriesList);
  }

  @Bean
  public SolrQueryBuilder caeSolrQueryBuilder(@Qualifier("filterQueryDefinitionMap") List<Map<String, Function<List<String>, String>>> filterQueryDefinitionMaps,
                                              @Qualifier("customSolrFields") List<Map<String, String>> customFields) {
    Map<String, Function<List<String>, String>> filterQueryDefinitionMap = filterQueryDefinitionMaps
            .stream()
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue));
    Map<String, String> customFieldsMap = customFields
            .stream()
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue));
    return new SolrCaeQueryBuilder("/cmdismax", filterQueryDefinitionMap, customFieldsMap);
  }

  @Bean
  public SolrQueryBuilder dynamicContentSolrQueryBuilder(@Qualifier("filterQueryDefinitionMap") List<Map<String, Function<List<String>, String>>> filterQueryDefinitionMaps,
                                                         @Qualifier("customSolrFields") List<Map<String, String>> customFields) {
    Map<String, Function<List<String>, String>> filterQueryDefinitionMap = filterQueryDefinitionMaps
            .stream()
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue));
    Map<String, String> customFieldsMap = customFields
            .stream()
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue));
    return new SolrCaeQueryBuilder("/select", filterQueryDefinitionMap, customFieldsMap);
  }

  @Bean
  @SuppressWarnings("squid:S00107")
  public QueryListAdapterFactory queryListAdapter(@Qualifier("queryListSearchResultFactory") SolrSearchResultFactory searchResultFactory,
                                                  ContentRepository contentRepository,
                                                  @Qualifier("settingsService") SettingsService settingsService,
                                                  SitesService sitesService,
                                                  List<IdScheme> idSchemes,
                                                  @Qualifier("dynamicContentSolrQueryBuilder") SolrQueryBuilder solrQueryBuilder,
                                                  @Qualifier("collectionExtendedItemsAdapter") ExtendedLinkListAdapterFactory collectionExtendedItemsAdapter,
                                                  @Qualifier("navigationAdapter") NavigationAdapterFactory navigationAdapterFactory,
                                                  @Qualifier("customStaticFilterQueries") List<List<FilterQueryArg>> customStaticFilterQueries) {
    List<FilterQueryArg> customStaticFilterQueriesList = customStaticFilterQueries.stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
    return new QueryListAdapterFactory(searchResultFactory, contentRepository, settingsService, sitesService, idSchemes, solrQueryBuilder, collectionExtendedItemsAdapter, navigationAdapterFactory, customStaticFilterQueriesList);
  }

  @Bean
  public SolrSearchResultFactory queryListSearchResultFactory(@Qualifier("solrClient") SolrClient solrClient,
                                                              ContentRepository contentRepository) {
    SolrSearchResultFactory solrSearchResultFactory = new SolrSearchResultFactory(contentRepository, solrClient, caasSearchConfigurationProperties.getSolr().getCollection());
    if (!caasServiceConfigurationProperties.isPreview()) {
      solrSearchResultFactory.setCacheForSeconds(this.caasSearchConfigurationProperties.getCache().getQuerylistSearchCacheForSeconds());
    }
    return solrSearchResultFactory;
  }

  @Bean
  public SolrSearchResultFactory searchResultFactory(@Qualifier("solrClient") SolrClient solrClient,
                                                     ContentRepository contentRepository) {
    SolrSearchResultFactory solrSearchResultFactory = new SolrSearchResultFactory(contentRepository, solrClient, caasSearchConfigurationProperties.getSolr().getCollection());
    if (!caasServiceConfigurationProperties.isPreview()) {
      solrSearchResultFactory.setCacheForSeconds(caasSearchConfigurationProperties.getCache().getSeconds());
    }
    return solrSearchResultFactory;
  }

  @Bean
  public ExtendedLinkListAdapterFactory collectionExtendedItemsAdapter() {
    return new ExtendedLinkListAdapterFactory("extendedItems", "links", "items", "CMLinkable", "target");
  }

  @Bean
  public NavigationAdapterFactory navigationAdapter(@Qualifier("contentContextStrategy") ContextStrategy<Content, Content> contextStrategy, Map<String, TreeRelation<Content>> treeRelations) {
    return new NavigationAdapterFactory(contextStrategy, treeRelations);
  }

  @Bean
  public IdScheme caasContentBeanIdScheme(ContentRepository contentRepository) {
    return new CaasContentBeanIdScheme(contentRepository);
  }

  @Bean
  public List<IdScheme> idSchemes(IdScheme caasContentBeanIdScheme) {
    return Collections.singletonList(caasContentBeanIdScheme);
  }

  @Bean
  public GraphQLScalarType FilterQueryArg() {
    return GraphQLScalarType.newScalar().name("FilterQueryArg").description("Built-in type for a custom search filter query argument").coercing(new CoercingFilterQueryArg()).build();
  }

  @Bean
  @Qualifier("filterQueryDefinitionMap")
  public Map<String, Function<List<String>, String>> solrFilterQueryDefinitionMap(TaxonomyAdapterFactory taxonomyAdapterFactory) {
    Map<String, Function<List<String>, String>> filterQueryDefinitionMap = new HashMap<>();
    filterQueryDefinitionMap.put("TITLE_OR", HeadlessSearchConfiguration::getTitleQuery);
    filterQueryDefinitionMap.put("EXCLUDE_IDS", HeadlessSearchConfiguration::getExcludeIdsQuery);
    filterQueryDefinitionMap.put("FRESHNESS", HeadlessSearchConfiguration::getFreshnessQuery);

    TaxonomyHelper taxonomyHelper = new TaxonomyHelper(taxonomyAdapterFactory);
    filterQueryDefinitionMap.put("SUBJ_TAXONOMY_OR", taxonomyHelper::getSubjectTaxonomyQuery);
    filterQueryDefinitionMap.put("LOC_TAXONOMY_OR", taxonomyHelper::getLocationTaxonomyQuery);
    return filterQueryDefinitionMap;
  }

  @Bean
  @Qualifier("customStaticFilterQueries")
  public List<FilterQueryArg> customStaticFilterQueries() {
    return Collections.emptyList();
  }

  @Bean
  @Qualifier("customSolrFields")
  public Map<String, String> customSolrFields() {
    return Collections.emptyMap();
  }

  private static String getTitleQuery(List<String> values) {
    if (values.size() == 1) {
      return SearchQueryHelper.exactQuery(SearchConstants.FIELDS.TITLE.toString(), "\"" + values.get(0) + "\"");
    } else {
      List<String> titles = values.stream().map(s -> "\"" + s + "\"").collect(Collectors.toList());
      return SearchQueryHelper.orQuery(SearchConstants.FIELDS.TITLE.toString(), titles);
    }
  }

  private static String getExcludeIdsQuery(List<String> values) {
    List<String> ids = values.stream()
            .map(val -> "\"contentbean:" + val + "\"")
            .collect(Collectors.toList());
    return SearchQueryHelper.negatedQuery(SearchQueryHelper.orQuery(SearchConstants.FIELDS.ID.toString(), ids));
  }

  private static String getFreshnessQuery(List<String> values) {
    if (values.size() == 2) {
      if ("*".equals(values.get(0))) {
        return SearchQueryHelper.validFromPastToValueQuery(SearchConstants.FIELDS.MODIFICATION_DATE.toString(), ZonedDateTime.parse(values.get(1)));
      }
      if ("*".equals(values.get(1))) {
        return SearchQueryHelper.validFromValueToFutureQuery(SearchConstants.FIELDS.MODIFICATION_DATE.toString(), ZonedDateTime.parse(values.get(0)));
      }
    }
    throw GraphqlErrorException.newErrorException().message(String.format("Cannot apply values %s to custom filter query.", values)).build();
  }
}
