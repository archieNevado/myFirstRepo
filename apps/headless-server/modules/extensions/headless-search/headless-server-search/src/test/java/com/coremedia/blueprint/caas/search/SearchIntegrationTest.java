package com.coremedia.blueprint.caas.search;

import com.coremedia.blueprint.base.caas.model.adapter.SearchResult;
import com.coremedia.blueprint.base.caas.model.adapter.SearchServiceAdapter;
import com.coremedia.blueprint.base.caas.model.adapter.SearchServiceAdapterFactory;
import com.coremedia.blueprint.base.caas.model.adapter.TaxonomyAdapterFactory;
import com.coremedia.caas.search.model.FilterQueryArg;
import com.coremedia.caas.search.solr.SolrSearchResultFactory;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import graphql.GraphQLContext;
import graphql.execution.DataFetcherResult;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.schema.DataFetchingEnvironment;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.caas.headless_server.plugin_support.PluginSupport.CONTEXT_PARAMETER_NAME_PREVIEW_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = SearchIntegrationTest.TestConfig.class)
@ExtendWith(SpringExtension.class)
public class SearchIntegrationTest {

  @Inject
  private SearchServiceAdapterFactory searchServiceAdapterFactory;

  @MockBean(name = "searchResultFactory")
  private SolrSearchResultFactory solrSearchResultFactory;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private QueryResponse queryResponse;

  @Mock
  private InstrumentationFieldFetchParameters parameters;

  @Mock
  private DataFetchingEnvironment dataFetchingEnvironment;

  @Mock
  private GraphQLContext graphQLContext;

  @BeforeEach
  public void setup() {
    when(dataFetchingEnvironment.getGraphQlContext()).thenReturn(graphQLContext);
    when(graphQLContext.get(CONTEXT_PARAMETER_NAME_PREVIEW_DATE)).thenReturn(ZonedDateTime.now());
    when(parameters.getEnvironment()).thenReturn(dataFetchingEnvironment);
    when(solrSearchResultFactory.createSearchResult(any(SolrQuery.class))).thenReturn(queryResponse);
  }

  @Test
  void searchConfigTest() {
    when(queryResponse.getResults().getNumFound()).thenReturn(1L);
    DataFetcherResult<SearchResult> searchResult = getSearchServiceAdapter().search("test", null, null, null, null, null, null);
    assertThat(searchResult.hasErrors()).isFalse();
    assertThat(searchResult.getData().getNumFound()).isEqualTo(1);
  }

  @Test
  void customFilterQueriesTest() {
    getSearchServiceAdapter().search("test", null, null, null, null, null, null);

    ArgumentCaptor<SolrQuery> solrQueryCaptor = ArgumentCaptor.forClass(SolrQuery.class);
    verify(solrSearchResultFactory).createSearchResult(solrQueryCaptor.capture());
    String[] filterQueries = solrQueryCaptor.getValue().getFilterQueries();
    assertThat(Arrays.stream(filterQueries).sequential()).anyMatch("-id:(\"contentbean:1234\" OR \"contentbean:5678\")"::equals);
  }

  @Test
  void customSolrFieldsTest() {
    getSearchServiceAdapter().search("test", null, null, null, Collections.singletonList("TESTFIELD_ASC"), null, null);

    ArgumentCaptor<SolrQuery> solrQueryCaptor = ArgumentCaptor.forClass(SolrQuery.class);
    verify(solrSearchResultFactory).createSearchResult(solrQueryCaptor.capture());
    SolrQuery.SortClause sortClause = solrQueryCaptor.getValue().getSorts().get(0);
    assertThat(sortClause.getItem()).isEqualTo("testfield");
    assertThat(sortClause.getOrder().name()).isEqualTo("asc");
  }

  @Test
  void customSolrFieldsTest_error() {
    DataFetcherResult<SearchResult> result = getSearchServiceAdapter().search("test", null, null, null, Collections.singletonList("INVALIDFIELD_DESC"), null, null, Collections.emptyList());
    assertThat(result.hasErrors()).isTrue();
  }

  private SearchServiceAdapter getSearchServiceAdapter() {
    SearchServiceAdapter searchServiceAdapter = searchServiceAdapterFactory.to();
    searchServiceAdapter.setDataFetchingEnvironment(dataFetchingEnvironment);
    return searchServiceAdapter;
  }

  @Configuration(proxyBeanMethods = false)
  @Import({XmlRepoConfiguration.class, HeadlessSearchConfiguration.class})
  public static class TestConfig {

    @Bean
    static XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig();
    }

    @Bean
    @Qualifier("customStaticFilterQueries")
    public List<FilterQueryArg> testCustomStaticFilterQueries() {
      return Collections.singletonList(new FilterQueryArg("EXCLUDE_IDS", Arrays.asList("1234", "5678")));
    }

    @Bean
    @Qualifier("customSolrFields")
    public Map<String, String> testSolrFields() {
      Map<String, String> customFields = new HashMap<>();
      customFields.put("TESTFIELD", "testfield");
      return customFields;
    }

    @Bean
    public TaxonomyAdapterFactory taxonomyAdapterFactory(ContentRepository contentRepository, SitesService sitesService) {
      return new TaxonomyAdapterFactory(contentRepository, sitesService, "path1", "path2");
    }
  }
}
