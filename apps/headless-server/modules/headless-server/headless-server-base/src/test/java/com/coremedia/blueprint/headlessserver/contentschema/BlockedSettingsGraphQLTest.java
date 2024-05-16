package com.coremedia.blueprint.headlessserver.contentschema;

import com.coremedia.blueprint.base.caas.CaasBlueprintBaseAutoConfiguration;
import com.coremedia.blueprint.base.caas.web.BlueprintBaseMediaConfig;
import com.coremedia.blueprint.coderesources.ThemeServiceConfiguration;
import com.coremedia.blueprint.headlessserver.CaasConfig;
import com.coremedia.caas.media.TransformationServiceConfiguration;
import com.coremedia.caas.search.solr.SolrQueryBuilder;
import com.coremedia.caas.search.solr.SolrSearchResultFactory;
import com.coremedia.caas.web.controller.graphql.GraphQLController;
import com.coremedia.caas.wrapper.UrlPathFormater;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dataloader.CacheMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.graphql.test.tester.WebGraphQlTester;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.CLANDESTINE_SETTINGS_ID;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.MASTER_SITE_ID;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.ROOT_CHANNEL_ID;

@SpringBootTest(classes = {
        CaasConfig.class,
        BlueprintBaseMediaConfig.class,
        CaasBlueprintBaseAutoConfiguration.class,
        GraphQLController.class,
        TransformationServiceConfiguration.class,
        ThemeServiceConfiguration.class,
        BlockedSettingsGraphQLTest.LocalTestConfiguration.class,
}, properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:/content/contentrepository.xml",
        "repository.params.userxml=classpath:/com/coremedia/cap/common/xml/users-default.xml"
})
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@ActiveProfiles("settings-test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SuppressWarnings("java:S2699") // assertions from spring-graphql are not detected by sonar
class BlockedSettingsGraphQLTest {

  @MockBean
  UrlPathFormater urlPathFormater;
  @MockBean
  ObjectMapper objectMapper;
  @MockBean
  CacheMap remoteLinkCacheMap;

  @MockBean(name = "caeSolrQueryBuilder")
  SolrQueryBuilder caeSolrQueryBuilder;
  @MockBean(name = "dynamicContentSolrQueryBuilder")
  SolrQueryBuilder dynamicContentSolrQueryBuilder;
  @MockBean(name = "suggestionsSolrQueryBuilder")
  SolrQueryBuilder suggestionsSolrQueryBuilder;
  @MockBean
  SolrSearchResultFactory solrSearchResultFactory;

  @Autowired
  private WebTestClient webTestClient;
  private WebGraphQlTester webGraphQlTester;

  @BeforeAll
  public void setUp() {
    webGraphQlTester = HttpGraphQlTester.builder(
                    webTestClient
                            .mutate()
                            .baseUrl("/graphql")
            )
            .header("Content-Type", "application/json")
            .build();
  }

  @Test
  void testGetBlockedSettingsById() {
    webGraphQlTester.documentName("settingsById")
            .variable("id", CLANDESTINE_SETTINGS_ID)
            .execute()
            .path("content.content").valueIsNull();
  }

  @Test
  void testGetBlockedSettingsViaLinkedSettings() {
    webGraphQlTester.documentName("linkedSettingsById")
            .variable("id", ROOT_CHANNEL_ID)
            .execute()
            .path("content.page.id").entity(Integer.class).isEqualTo(ROOT_CHANNEL_ID)
            .path("content.page.settings.clandestine").valueIsNull();
  }

  @Test
  void testGetBlockedSettingsViaSite() {
    webGraphQlTester.documentName("linkedSettingsBySite")
            .variable("siteId", MASTER_SITE_ID)
            .execute()
            .path("content.site.id").entity(String.class).isEqualTo(MASTER_SITE_ID)
            .path("content.site.root.id").entity(Integer.class).isEqualTo(ROOT_CHANNEL_ID)
            .path("content.site.root.settings.clandestine").valueIsNull();
  }

  @Configuration(proxyBeanMethods = false)
  public static class LocalTestConfiguration {
    @Bean
    BeanResolver pluginSchemaAdapterBeansResolver(BeanFactory beanFactory) {
      return new BeanFactoryResolver(beanFactory);
    }
  }
}
