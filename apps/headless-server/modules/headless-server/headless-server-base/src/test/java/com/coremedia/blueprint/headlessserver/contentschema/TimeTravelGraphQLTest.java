package com.coremedia.blueprint.headlessserver.contentschema;

import com.coremedia.blueprint.base.caas.web.BlueprintBaseMediaConfig;
import com.coremedia.blueprint.coderesources.ThemeServiceConfiguration;
import com.coremedia.blueprint.headlessserver.CaasConfig;
import com.coremedia.caas.headless_server.plugin_support.extensionpoints.CopyToContextParameter;
import com.coremedia.caas.media.TransformationServiceConfiguration;
import com.coremedia.caas.web.controller.graphql.GraphQLController;
import com.coremedia.caas.web.wiring.PreviewDateContextParameter;
import com.coremedia.caas.wrapper.UrlPathFormater;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dataloader.CacheMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.ZonedDateTime;

import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.ARTICLE_REPO_PATH;
import static com.coremedia.blueprint.headlessserver.contentschema.TestRepoConstants.TIME_TRAVEL_ARTICLE_ID;
import static com.coremedia.caas.headless_server.plugin_support.PluginSupport.HTTP_HEADER_NAME_X_PREVIEW_DATE;
import static com.coremedia.caas.headless_server.plugin_support.PluginSupport.QUALIFIER_CAAS_COPY_TO_CONTEXT_PARAMETER;

@SpringBootTest(classes = {
        CaasConfig.class,
        BlueprintBaseMediaConfig.class,
        GraphQLController.class,
        TransformationServiceConfiguration.class,
        ThemeServiceConfiguration.class,
        TimeTravelGraphQLTest.LocalTestConfiguration.class,
}, properties = {
        "repository.factoryClassName=com.coremedia.cap.xmlrepo.XmlCapConnectionFactory",
        "repository.params.contentxml=classpath:/content/contentrepository.xml",
        "repository.params.userxml=classpath:/com/coremedia/cap/common/xml/users-default.xml",
        "caas.preview=true",
})
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SuppressWarnings("java:S2699") // assertions from spring-graphql are not detected by sonar
class TimeTravelGraphQLTest {

  @MockBean
  UrlPathFormater urlPathFormater;
  @MockBean
  ObjectMapper objectMapper;
  @MockBean
  CacheMap remoteLinkCacheMap;

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
  void testGetArticleByIdCurrentPreviewDate() {
    webGraphQlTester.documentName("articleById")
            .variable("id", TIME_TRAVEL_ARTICLE_ID)
            .execute()
            .path("content.article").valueIsNull();
  }

  @Test
  void testGetArticleByIdWithFuturePreviewDate() {
    webGraphQlTester
            .mutate()
            .header(HTTP_HEADER_NAME_X_PREVIEW_DATE, "Fri, 01 Jan 2100 00:00:00 GMT").build()
            .documentName("articleById")
            .variable("id", TIME_TRAVEL_ARTICLE_ID)
            .execute()
            .path("content.article.id").entity(Integer.class).isEqualTo(TIME_TRAVEL_ARTICLE_ID)
            .path("content.article.type").entity(String.class).isEqualTo("CMArticle")
            .path("content.article.repositoryPath").entity(String.class).isEqualTo(ARTICLE_REPO_PATH);
  }

  @Configuration(proxyBeanMethods = false)
  public static class LocalTestConfiguration {
    @Bean
    BeanResolver pluginSchemaAdapterBeansResolver(BeanFactory beanFactory) {
      return new BeanFactoryResolver(beanFactory);
    }

    @Bean
    @Qualifier(QUALIFIER_CAAS_COPY_TO_CONTEXT_PARAMETER)
    public CopyToContextParameter<Long, ZonedDateTime> previewDateContextParameter() {
      return new PreviewDateContextParameter();
    }
  }
}
