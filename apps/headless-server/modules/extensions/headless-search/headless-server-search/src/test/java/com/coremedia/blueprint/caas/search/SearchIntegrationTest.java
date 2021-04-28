package com.coremedia.blueprint.caas.search;

import com.coremedia.blueprint.base.caas.model.adapter.SearchResult;
import com.coremedia.blueprint.base.caas.model.adapter.SearchServiceAdapterFactory;
import com.coremedia.caas.search.solr.SolrSearchResultFactory;
import com.coremedia.caas.wiring.ContextInstrumentation;
import com.coremedia.cap.test.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.test.xmlrepo.XmlUapiConfig;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.schema.DataFetchingEnvironment;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = SearchIntegrationTest.TestConfig.class)
@Import(HeadlessSearchConfiguration.class)
@ExtendWith({SpringExtension.class})
public class SearchIntegrationTest {

  @Inject
  private SearchServiceAdapterFactory searchServiceAdapterFactory;

  @Inject
  private ContextInstrumentation contextInstrumentation;

  @MockBean(name = "searchResultFactory")
  private SolrSearchResultFactory solrSearchResultFactory;

  @Mock
  private QueryResponse queryResponse;

  @Mock
  private SolrDocument solrDocument;

  @Mock
  private InstrumentationFieldFetchParameters parameters;

  @Mock
  private DataFetchingEnvironment environment;

  @BeforeEach
  public void setup() {
    when(parameters.getEnvironment()).thenReturn(environment);
    when(environment.getContext()).thenReturn(new HashMap<>());
    contextInstrumentation.beginFieldFetch(parameters);

    when(solrSearchResultFactory.createSearchResult(any(SolrQuery.class))).thenReturn(queryResponse);
    SolrDocumentList t = new SolrDocumentList();
    t.add(solrDocument);
    t.setNumFound(1);
    // use solr index of cae
    when(solrDocument.getFieldValue("id")).thenReturn("contentbean:2");
    when(queryResponse.getResults()).thenReturn(t);
  }

  @Test
  void searchConfigTest() {
    SearchResult searchResult = searchServiceAdapterFactory.to().search("test", null, null, null, null, null, null);
    assertEquals(1, searchResult.getNumFound());
  }

  @Configuration(proxyBeanMethods = false)
  @Import({XmlRepoConfiguration.class})
  public static class TestConfig {

    @Bean
    static XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/blueprint/caas/search/contentrepository.xml");
    }

    @Bean
    public ContextInstrumentation contextInstrumentation() {
      return new ContextInstrumentation();
    }
  }
}
